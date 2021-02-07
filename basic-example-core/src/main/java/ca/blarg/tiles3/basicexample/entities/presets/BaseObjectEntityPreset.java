package ca.blarg.tiles3.basicexample.entities.presets;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.entities.EntityPreset;
import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventListener;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.tiles3.basicexample.entities.events.DespawnedEvent;
import ca.blarg.tiles3.basicexample.entities.events.SpawnedEvent;
import com.badlogic.gdx.utils.Disposable;

public abstract class BaseObjectEntityPreset extends EntityPreset implements EventListener, Disposable {
	public final EventManager eventManager;

	public BaseObjectEntityPreset(EntityManager entityManager) {
		super(entityManager);
		eventManager = entityManager.eventManager;

		eventManager.addListener(SpawnedEvent.class, this);
		eventManager.addListener(DespawnedEvent.class, this);
	}

	@Override
	public void dispose() {
		eventManager.removeListener(SpawnedEvent.class, this);
		eventManager.removeListener(DespawnedEvent.class, this);
	}

	public void onSpawn(Entity entity) {
	}

	public void onDespawn(Entity entity) {
	}

	@Override
	public boolean handle(Event e) {
		if (e instanceof SpawnedEvent) {
			SpawnedEvent event = (SpawnedEvent)e;
			if (event.entity.getPresetUsedToCreate() == this.getClass())
				onSpawn(event.entity);
		}

		else if (e instanceof DespawnedEvent) {
			DespawnedEvent event = (DespawnedEvent)e;
			if (event.entity.getPresetUsedToCreate() == this.getClass())
				onDespawn(event.entity);
		}

		return false;
	}
}
