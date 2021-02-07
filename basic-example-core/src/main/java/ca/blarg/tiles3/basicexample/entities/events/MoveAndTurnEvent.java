package ca.blarg.tiles3.basicexample.entities.events;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.events.Event;

public class MoveAndTurnEvent extends Event {
	public Entity entity;
	public float angle;

	@Override
	public void reset() {
		entity = null;
		angle = 0.0f;
	}
}
