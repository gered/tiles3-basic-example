package ca.blarg.tiles3.basicexample.entities.events;

import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.events.Event;

public class MoveInDirectionEvent extends Event {
	public Entity entity;
	public final Vector3 direction = new Vector3();

	@Override
	public void reset() {
		entity = null;
		direction.set(Vector3.Zero);
	}
}
