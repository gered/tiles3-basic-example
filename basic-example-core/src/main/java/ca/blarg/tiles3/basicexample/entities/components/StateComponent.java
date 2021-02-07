package ca.blarg.tiles3.basicexample.entities.components;

import ca.blarg.gdx.entities.Component;
import ca.blarg.tiles3.basicexample.entities.EntityState;

public class StateComponent extends Component {
	public EntityState state;
	public EntityState actionState;
	public boolean isInActionState;

	@Override
	public void reset() {
		state = EntityState.None;
		actionState = EntityState.None;
		isInActionState = false;
	}
}
