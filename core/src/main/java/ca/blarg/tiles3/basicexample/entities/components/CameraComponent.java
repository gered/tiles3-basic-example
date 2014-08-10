package ca.blarg.tiles3.basicexample.entities.components;

import ca.blarg.gdx.entities.Component;

public class CameraComponent extends Component {
	public boolean useEntityOrientation;
	public float yAngle;

	@Override
	public void reset() {
		useEntityOrientation = false;
		yAngle = 0.0f;
	}
}
