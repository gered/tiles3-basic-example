package ca.blarg.tiles3.basicexample.entities.components.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Sphere;
import ca.blarg.gdx.entities.Component;

public class BoundingSphereComponent extends Component {
	public final Sphere bounds = new Sphere(Vector3.Zero, 0.0f);

	@Override
	public void reset() {
		bounds.center.set(Vector3.Zero);
		bounds.radius = 0.0f;
	}
}
