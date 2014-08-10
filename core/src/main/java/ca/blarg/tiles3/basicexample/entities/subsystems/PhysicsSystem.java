package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.gdx.math.SweptSphereHandler;
import ca.blarg.gdx.tilemap3d.TileMapSweptSphereCollisionChecker;
import ca.blarg.tiles3.basicexample.entities.EntityState;
import ca.blarg.tiles3.basicexample.entities.PhysicsConstants;
import ca.blarg.tiles3.basicexample.entities.components.WorldComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.*;
import ca.blarg.tiles3.basicexample.entities.events.*;
import ca.blarg.tiles3.basicexample.entities.forces.Force;
import ca.blarg.tiles3.basicexample.entities.forces.JumpForce;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pools;

public class PhysicsSystem extends ComponentSystem {
	private class TerrainFrictionValues {
		public float friction;
		public float walkAcceleration;
		public float maxWalkSpeed;
	}

	final SweptSphereHandler sweptSphereHandler;
	public final TileMapSweptSphereCollisionChecker sweptSphereWorldCollisionChecker;

	static final Vector3 tmp1 = new Vector3();
	final TerrainFrictionValues tmpTerrainFrictionValues = new TerrainFrictionValues();

	public PhysicsSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
		listenFor(MoveForwardEvent.class);
		listenFor(MoveInDirectionEvent.class);
		listenFor(MoveAndTurnEvent.class);
		listenFor(StopMovementEvent.class);
		listenFor(JumpEvent.class);

		sweptSphereWorldCollisionChecker = new TileMapSweptSphereCollisionChecker();
		sweptSphereHandler = new SweptSphereHandler(sweptSphereWorldCollisionChecker, 5);
		sweptSphereHandler.possibleCollisionAreaMinOffset.set(0.0f, -0.5f, 0.0f);
		sweptSphereHandler.possibleCollisionAreaMaxOffset.set(0.0f, 0.5f, 0.0f);
	}

	@Override
	public void dispose() {
		stopListeningFor(MoveForwardEvent.class);
		stopListeningFor(MoveInDirectionEvent.class);
		stopListeningFor(MoveAndTurnEvent.class);
		stopListeningFor(StopMovementEvent.class);
		stopListeningFor(JumpEvent.class);
	}

	static final Vector3 resultingVelocity = new Vector3();
	static final Vector3 gravityVelocity = new Vector3();
	@Override
	public void onUpdateGameState(float delta) {
		Entity worldEntity = entityManager.getFirstWith(WorldComponent.class);
		if (worldEntity == null)
			return;   // no world to simulate physics for!

		WorldComponent world = worldEntity.get(WorldComponent.class);
		sweptSphereWorldCollisionChecker.tileMap = world.tileMap;

		for (Entity entity : entityManager.getAllWith(PhysicsComponent.class)) {
			PhysicsComponent physics = entity.get(PhysicsComponent.class);
			PositionComponent position = entity.get(PositionComponent.class);

			// to accurately check if the entity is walking we need to check this first!
			// walking only adds velocity in the XZ plane. forces can add velocity in any
			// direction, so if we added them first, we couldn't just check XZ
			updateIsWalkingState(physics);

			// apply forces. this directly affects velocity, so it needs to go next!
			processForces(physics, delta);

			getTerrainFrictionFor(physics, tmpTerrainFrictionValues);

			// apply friction and then combine walking and force velocity's into one
			// to get the entity's current velocity for this tick
			physics.walkingVelocity.scl(tmpTerrainFrictionValues.friction);

			if (physics.walkingVelocity.len() <= 0.12f)
				physics.walkingVelocity.set(Vector3.Zero);
			if (physics.forceVelocity.len() <= 0.1f)
				physics.forceVelocity.set(Vector3.Zero);

			physics.velocity.set(physics.walkingVelocity)
			                .add(physics.forceVelocity);

			// update last/current tick velocities
			physics.lastTickVelocity.set(physics.currentTickVelocity);
			physics.currentTickVelocity.set(physics.velocity)
			                           .scl(delta);

			Vector3 gravity = gravityVelocity.set(PhysicsConstants.GRAVITY)
			                                 .scl(delta);

			physics.sweptSphere.position.set(position.position);

			resultingVelocity.set(Vector3.Zero);
			sweptSphereHandler.handleMovement(physics.sweptSphere,
			                                  physics.currentTickVelocity,
			                                  gravity,
			                                  resultingVelocity,
			                                  true,
			                                  true,
			                                  PhysicsConstants.SLOPE_STEEP_Y_ANGLE);

			position.position.set(physics.sweptSphere.position);
			physics.currentTickVelocity.set(resultingVelocity);
			physics.collisionTilePosition.set(sweptSphereWorldCollisionChecker.lastCollisionTilePosition);

			physics.isInMotion = physics.sweptSphere.isInMotion;
			physics.wasInMotion = physics.sweptSphere.wasInMotion;
			physics.isFalling = physics.sweptSphere.isFalling;
			physics.wasFalling = physics.sweptSphere.wasFalling;
			physics.isSliding = physics.sweptSphere.isSliding;
			physics.wasSliding = physics.sweptSphere.wasSliding;
			physics.isOnGround = physics.sweptSphere.isOnGround;
			physics.wasOnGround = physics.sweptSphere.wasOnGround;

			updateStandingTileCoords(physics, position, world);

			triggerStateChangeEvents(physics, entity);
		}
	}

	@Override
	public boolean handle(Event e) {
		if (e instanceof MoveInDirectionEvent)
			return handle((MoveInDirectionEvent)e);

		else if (e instanceof MoveForwardEvent)
			return handle((MoveForwardEvent)e);

		else if (e instanceof MoveAndTurnEvent)
			return handle((MoveAndTurnEvent)e);

		else if (e instanceof StopMovementEvent)
			return handle((StopMovementEvent)e);

		else if (e instanceof JumpEvent)
			return handle((JumpEvent)e);

		return false;
	}

	private boolean handle(MoveInDirectionEvent e) {
		if (e.entity.has(PhysicsComponent.class)) {
			move(e.entity, e.direction);
			return true;
		} else
			return false;
	}

	private boolean handle(MoveForwardEvent e) {
		if (e.entity.has(PhysicsComponent.class)) {
			OrientationXZComponent orientation = e.entity.get(OrientationXZComponent.class);
			tmp1.set(Vector3.Zero);
			MathHelpers.getDirectionVector3FromYAxis(orientation.angle, tmp1);
			move(e.entity, tmp1);
			return true;
		} else
			return false;
	}

	private boolean handle(MoveAndTurnEvent e) {
		if (e.entity.has(PhysicsComponent.class)) {
			OrientationXZComponent orientation = e.entity.get(OrientationXZComponent.class);
			orientation.angle = e.angle;
			tmp1.set(Vector3.Zero);
			MathHelpers.getDirectionVector3FromYAxis(orientation.angle, tmp1);
			move(e.entity, tmp1);
			return true;
		} else
			return false;
	}

	private boolean handle(StopMovementEvent e) {
		if (e.entity.has(PhysicsComponent.class)) {
			stop(e.entity);
			return true;
		} else
			return false;
	}

	private boolean handle(JumpEvent e) {
		if (e.entity.has(PhysicsComponent.class)) {
			jump(e.entity);
			return true;
		} else
			return false;
	}

	private void triggerStateChangeEvents(PhysicsComponent physics, Entity entity) {
		if (physics.isFalling && !physics.wasFalling) {
			EntityStateChangeEvent event = eventManager.create(EntityStateChangeEvent.class);
			event.state = EntityState.Falling;
			event.entity = entity;
			eventManager.trigger(event);
		} else if (physics.wasFalling && physics.isWalking) {
			EntityStateChangeEvent event = eventManager.create(EntityStateChangeEvent.class);
			event.state = EntityState.Walking;
			event.entity = entity;
			eventManager.trigger(event);
		} else if (physics.wasFalling && !physics.isFalling) {
			EntityStateChangeEvent event = eventManager.create(EntityStateChangeEvent.class);
			event.state = EntityState.Idle;
			event.entity = entity;
			eventManager.trigger(event);
		} else if (physics.isWalking && !physics.wasWalking && !physics.isFalling) {
			EntityStateChangeEvent event = eventManager.create(EntityStateChangeEvent.class);
			event.state = EntityState.Walking;
			event.entity = entity;
			eventManager.trigger(event);
		} else if (physics.wasWalking && !physics.isWalking) {
			EntityStateChangeEvent event = eventManager.create(EntityStateChangeEvent.class);
			event.state = EntityState.Idle;
			event.entity = entity;
			eventManager.trigger(event);
		}
	}

	static final Vector3 tmpMovementVelocity = new Vector3();
	private void move(Entity entity, Vector3 direction) {
		PhysicsComponent physics = entity.get(PhysicsComponent.class);

		getTerrainFrictionFor(physics, tmpTerrainFrictionValues);

		float acceleration = tmpTerrainFrictionValues.walkAcceleration;
		float maxSpeed = tmpTerrainFrictionValues.maxWalkSpeed;

		// currentVelocity = a * maxVelocity + (1 - a) * currentVelocity

		tmpMovementVelocity.set(direction).scl(maxSpeed)   // maxVelocity
		                   .scl(acceleration);             // * a

		physics.walkingVelocity.scl(1 - acceleration)      // (1 - a) * currentVelocity
		                       .add(tmpMovementVelocity);  // + (a * maxVelocity)
	}

	private void stop(Entity entity) {
		PhysicsComponent physics = entity.get(PhysicsComponent.class);
		physics.velocity.set(Vector3.Zero);
		physics.walkingVelocity.set(Vector3.Zero);
	}

	private void jump(Entity entity) {
		PhysicsComponent physics = entity.get(PhysicsComponent.class);

		if (!physics.isOnGround)
			return;

		JumpForce newForce = Pools.obtain(JumpForce.class);
		newForce.set(MathHelpers.UP_VECTOR3, PhysicsConstants.GRAVITY_SPEED * 3.0f, 0.6f, physics);
		applyForce(physics, newForce);
	}

	private void applyForce(PhysicsComponent physics, Force force) {
		physics.forces.add(force);
	}

	private void applyForce(PhysicsComponent physics, Vector3 direction, float strength, float friction) {
		Force newForce = Pools.obtain(Force.class);
		newForce.set(direction, strength, friction);
		applyForce(physics, newForce);
	}

	private void processForces(PhysicsComponent physics, float delta) {
		physics.forceVelocity.set(Vector3.Zero);
		for (Force force : physics.forces) {
			force.update(delta);
			physics.forceVelocity.add(force.getCurrentForce());
		}

		// clean up dead forces
		int i = 0;
		while (i < physics.forces.size) {
			Force force = physics.forces.get(i);
			if (!force.isActive()) {
				// stay at this index for the next iteration since we just removed something (next element now at this same index)
				physics.forces.removeIndex(i);
				Pools.free(force);
			} else
				++i;
		}
	}

	private void updateIsWalkingState(PhysicsComponent physics) {
		physics.wasWalking = physics.isWalking;

		if (!physics.isOnGround) {
			physics.isWalking = false;
			return;
		}

		tmp1.set(physics.walkingVelocity);
		tmp1.y = 0.0f;   // walking only allows the entity to get a velocity in the XZ plane

		float velocityLength = tmp1.len();
		if (MathHelpers.areAlmostEqual(velocityLength, 0.0f))
			physics.isWalking = false;
		else
			physics.isWalking = true;
	}

	static final Vector3 tmpFeetPosition = new Vector3();
	static final Ray downRay = new Ray(Vector3.Zero, Vector3.Zero);
	static final Vector3 tmpCollisionPoint = new Vector3();
	private void updateStandingTileCoords(PhysicsComponent physics, PositionComponent position, WorldComponent world) {
		if (physics.isOnGround) {
			int feetX = (int)position.position.x;
			int feetY = (int)(position.position.y - physics.sweptSphere.radius.y - 0.01f);
			int feetZ = (int)position.position.z;

			// prefer to take the tile just below the entity's feet
			// however, if that tile is empty, take the collision tile
			// (this can sometimes work better, as the collision tile could
			// be a tile that the entity is just standing on the very edge of, but
			// in fact be mostly standing on an adjacent tile... which could be the
			// one we calculate to be under the entity's feet)

			physics.standingOnTile = world.tileMap.get(feetX, feetY, feetZ);
			if (!physics.standingOnTile.isEmptySpace()) {
				// check the distance from the entity's feet to the nearest
				// collidable surface on this tile before we accept it as the
				// "standing on" tile
				tmpFeetPosition.set(position.position);
				tmpFeetPosition.y -= physics.sweptSphere.radius.y;
				downRay.set(tmpFeetPosition, MathHelpers.DOWN_VECTOR3);
				tmpCollisionPoint.set(Vector3.Zero);
				float collisionDistance = 0.0f;
				if (world.tileMap.checkForCollisionWithTileMesh(downRay, feetX, feetY, feetZ, world.tileMap.tileMeshes, tmpCollisionPoint))
					collisionDistance = tmp1.set(tmpFeetPosition)
					                        .sub(tmpCollisionPoint)
					                        .len();

				// TODO: this distance check causes issues with ramps. as you walk
				//       up/down the ramp, the "standing tile" tends to flip back
				//       and forth a bit depending on how far up or down the ramp
				//       you are from the top, assuming the top of the ramp is
				//       next to a flat surface
				if (collisionDistance <= 0.1f) {
					// the top of the foot tile is close enough, use it
					physics.standingOnTilePosition.set(feetX, feetY, feetZ);
					return;
				}
			}

			// use the collision tile
			physics.standingOnTile = world.tileMap.get(physics.collisionTilePosition.x, physics.collisionTilePosition.y, physics.collisionTilePosition.z);
			physics.standingOnTilePosition.set(physics.collisionTilePosition);
		} else {
			// not standing on anything
			physics.standingOnTilePosition.set(0, 0, 0);
			physics.standingOnTile = null;
		}
	}

	private void getTerrainFrictionFor(PhysicsComponent physics, final TerrainFrictionValues out) {
		out.friction = physics.friction;
		out.walkAcceleration = physics.walkingAcceleration;
		out.maxWalkSpeed = physics.maxWalkSpeed;

		if (physics.isOnGround) {
			if (physics.standingOnTile.isSlippery()) {
				out.friction = 0.95f;
				out.walkAcceleration = 0.5f;
				out.maxWalkSpeed = 2.0f;
			}
		}
	}
}
