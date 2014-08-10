package ca.blarg.tiles3.basicexample.entities.events;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.events.Event;
import ca.blarg.tiles3.basicexample.entities.EntityState;

public class AnimationChangeEvent extends Event {
	public Entity entity;
	public String sequenceName;
	public boolean changeToSequenceForState;
	public EntityState state;
	public boolean loop;
	public boolean overrideExisting;

	@Override
	public void reset() {
		entity = null;
		sequenceName = null;
		changeToSequenceForState = false;
		state = EntityState.None;
		loop = false;
		overrideExisting = false;
	}
}
