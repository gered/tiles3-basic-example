package ca.blarg.tiles3.basicexample.entities.components.physics;

import ca.blarg.gdx.entities.Component;
import ca.blarg.gdx.math.SweptSphere;
import ca.blarg.gdx.tilemap3d.Tile;
import ca.blarg.gdx.tilemap3d.TileCoord;
import ca.blarg.tiles3.basicexample.entities.forces.Force;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class PhysicsComponent extends Component {
	public final Vector3 velocity = new Vector3();
	public final Vector3 lastTickVelocity = new Vector3();
	public final Vector3 currentTickVelocity = new Vector3();

	public final Vector3 walkingVelocity = new Vector3();
	public float walkingAcceleration;
	public float maxWalkSpeed;
	public float friction;

	public final Array<Force> forces = new Array<Force>(false, 4, Force.class);
	public final Vector3 forceVelocity = new Vector3();

	public final SweptSphere sweptSphere = new SweptSphere();
	public final TileCoord collisionTilePosition = new TileCoord();
	public final TileCoord standingOnTilePosition = new TileCoord();
	public Tile standingOnTile;

	public boolean isWalking;
	public boolean wasWalking;
	public boolean isInMotion;
	public boolean wasInMotion;
	public boolean isFalling;
	public boolean wasFalling;
	public boolean isOnGround;
	public boolean wasOnGround;
	public boolean isSliding;
	public boolean wasSliding;

	public float fallDistance;
	public float lastYPosition;
	public float currentFallDistance;

	public final Ray slidingPlaneNormal = new Ray(Vector3.Zero, Vector3.Zero);
	public final Vector3 nearestIntersection = new Vector3();

	public PhysicsComponent setMovementProperties(float walkingAcceleration, float maxWalkSpeed, float friction) {
		this.walkingAcceleration = walkingAcceleration;
		this.maxWalkSpeed = maxWalkSpeed;
		this.friction = friction;
		return this;
	}

	public PhysicsComponent setBoundsRadius(float radius) {
		sweptSphere.setRadius(radius);
		return this;
	}

	@Override
	public void reset() {
		velocity.set(Vector3.Zero);
		lastTickVelocity.set(Vector3.Zero);
		currentTickVelocity.set(Vector3.Zero);
		walkingVelocity.set(Vector3.Zero);
		walkingAcceleration = 0.0f;
		maxWalkSpeed = 0.0f;
		friction = 0.0f;
		for (int i = 0; i < forces.size; ++i)
			Pools.free(forces.get(i));
		forces.clear();
		forceVelocity.set(Vector3.Zero);
		sweptSphere.reset();
		collisionTilePosition.set(0, 0, 0);
		standingOnTilePosition.set(0, 0, 0);
		standingOnTile = null;
		isWalking = false;
		wasWalking = false;
		isInMotion = false;
		wasInMotion = false;
		isFalling = false;
		wasFalling = false;
		isOnGround = false;
		wasOnGround = false;
		isSliding = false;
		wasSliding = false;
		fallDistance = 0.0f;
		lastYPosition = 0.0f;
		currentFallDistance = 0.0f;
		slidingPlaneNormal.origin.set(Vector3.Zero);
		slidingPlaneNormal.direction.set(Vector3.Zero);
		nearestIntersection.set(Vector3.Zero);
	}
}
