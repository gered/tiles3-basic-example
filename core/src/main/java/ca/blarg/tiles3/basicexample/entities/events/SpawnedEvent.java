package ca.blarg.tiles3.basicexample.entities.events;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.events.Event;

public class SpawnedEvent extends Event {
	public Entity entity;

	@Override
	public void reset() {
		entity = null;
	}
}
