package ca.blarg.tiles3.basicexample.entities.components.physics;

import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.entities.Component;

public class PositionComponent extends Component {
	public final Vector3 position = new Vector3();
	public final Vector3 lastPosition = new Vector3();

	@Override
	public void reset() {
		position.set(Vector3.Zero);
		lastPosition.set(Vector3.Zero);
	}
}
