package ca.blarg.tiles3.basicexample.entities.components.physics;

import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.entities.Component;
import ca.blarg.gdx.math.MathHelpers;

public class OrientationXZComponent extends Component {
	public float angle = 0.0f;

	public void getDirectionVector(Vector3 out) {
		MathHelpers.getDirectionVector3FromYAxis(angle, out);
	}

	@Override
	public void reset() {
		angle = 0.0f;
	}
}
