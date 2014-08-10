package ca.blarg.tiles3.basicexample.entities.presets;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.tiles3.basicexample.ContentCache;
import ca.blarg.tiles3.basicexample.entities.EntityState;
import ca.blarg.tiles3.basicexample.entities.PhysicsConstants;
import ca.blarg.tiles3.basicexample.entities.components.CameraComponent;
import ca.blarg.tiles3.basicexample.entities.components.PlayerComponent;
import ca.blarg.tiles3.basicexample.entities.components.StateComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.*;
import ca.blarg.tiles3.basicexample.entities.components.rendering.*;

public class PlayerPreset extends BaseObjectEntityPreset {
	public PlayerPreset(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public Entity create(CreationArgs args) {
		ContentCache content = Services.get(ContentCache.class);

		Entity entity = entityManager.add();
		entity.add(BoundingSphereComponent.class).bounds.radius = 0.5f;
		entity.add(StateComponent.class).state = EntityState.Idle;
		entity.add(PlayerComponent.class);

		CameraComponent camera = entity.add(CameraComponent.class);
		camera.useEntityOrientation = false;

		BillboardComponent billboard = entity.add(BillboardComponent.class);
		billboard.isAxisAligned = true;
		billboard.atlas = content.player;
		billboard.width = 1.7f;
		billboard.height = 1.7f;

		AnimationComponent animations = entity.add(AnimationComponent.class);
		animations.sequences.add("idle").set(0, 0, 0.0f, true, 0);
		animations.sequences.add("walk").set(8, 9, 0.15f, true, 0);
		animations.sequences.add("dead").set(32, 32, 0.0f);
		animations.sequences.add("jump").set(8, 8, 0.0f, true, 1);
		animations.sequences.add("fall").set(9, 9, 0.0f, true, 1);
		animations.setSequence("idle", true, false);

		AutoAnimationComponent autoAnimation = entity.add(AutoAnimationComponent.class);
		autoAnimation.set(EntityState.Idle, "idle", true, false)
		             .set(EntityState.Walking, "walk", true, false)
		             .set(EntityState.Falling, "fall", true, false)
		             .set(EntityState.Jumping, "jump", true, false)
		             .set(EntityState.Dead, "dead", false, true);

		entity.add(PhysicsComponent.class)
		      .setMovementProperties(1.0f, 8.0f, PhysicsConstants.FRICTION_NORMAL)
		      .setBoundsRadius(0.49f);

		PositionComponent position = entity.add(PositionComponent.class);
		position.position.set(16.0f, 1.5f, 16.0f);
		position.lastPosition.set(position.position);

		entity.add(OrientationXZComponent.class);

		return entity;
	}
}
