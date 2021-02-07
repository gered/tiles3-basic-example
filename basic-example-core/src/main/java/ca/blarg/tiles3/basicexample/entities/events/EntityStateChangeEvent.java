package ca.blarg.tiles3.basicexample.entities.events;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.events.Event;
import ca.blarg.tiles3.basicexample.entities.EntityState;

public class EntityStateChangeEvent extends Event {
	public Entity entity;
	public EntityState state;
	public boolean isActionState;
	public boolean clearExistingActionStateInfo;

	@Override
	public void reset() {
		entity = null;
		state = EntityState.None;
		isActionState = false;
		clearExistingActionStateInfo = false;
	}
}
