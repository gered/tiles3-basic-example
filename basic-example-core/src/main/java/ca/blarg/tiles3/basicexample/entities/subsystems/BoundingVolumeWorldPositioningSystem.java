package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.tiles3.basicexample.entities.components.physics.BoundingSphereComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.PositionComponent;

public class BoundingVolumeWorldPositioningSystem extends ComponentSystem {
	public BoundingVolumeWorldPositioningSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
	}

	@Override
	public void onUpdateGameState(float delta) {
		for (Entity entity : entityManager.getAllWith(BoundingSphereComponent.class)) {
			BoundingSphereComponent bounds = entity.get(BoundingSphereComponent.class);
			PositionComponent position = entity.get(PositionComponent.class);

			bounds.bounds.center.set(position.position);
		}
	}
}
