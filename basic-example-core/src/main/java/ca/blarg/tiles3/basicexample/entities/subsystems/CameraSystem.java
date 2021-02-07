package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.EulerPerspectiveCamera;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.tiles3.basicexample.entities.EntityUtils;
import ca.blarg.tiles3.basicexample.entities.components.CameraComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.OrientationXZComponent;
import com.badlogic.gdx.math.Vector3;

public class CameraSystem extends ComponentSystem {
	static final float FOLLOW_DISTANCE = 3.5f;
	static final float FOLLOW_HEIGHT = 9.0f;
	static final float FOLLOW_PITCH_ANGLE = 70.0f;

	final static Vector3 targetEntityDirection = new Vector3();
	final static Vector3 targetEntityPosition = new Vector3();
	final static Vector3 tmp = new Vector3();

	final ViewportContext viewportContext;

	public CameraSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
		viewportContext = Services.get(ViewportContext.class);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onRender(float interpolation) {
		Entity target = entityManager.getFirstWith(CameraComponent.class);
		if (target == null)
			return;

		CameraComponent cameraProperties = target.get(CameraComponent.class);

		float yAngle;
		if (cameraProperties.useEntityOrientation)
			yAngle = target.get(OrientationXZComponent.class).angle;
		else
			yAngle = cameraProperties.yAngle;

		EulerPerspectiveCamera camera = (EulerPerspectiveCamera)viewportContext.getPerspectiveCamera();

		MathHelpers.getDirectionVector3FromYAxis(yAngle, targetEntityDirection);
		EntityUtils.getInterpolatedPosition(target, interpolation, targetEntityPosition);

		// get the new camera position
		tmp.set(targetEntityDirection)              // behind the direction the player is currently facing
		   .scl(-1.0f);
		MathHelpers.setLengthOf(tmp, FOLLOW_DISTANCE);   // FOLLOW_DISTANCE units behind ...
		tmp.add(0.0f, FOLLOW_HEIGHT, 0.0f)               // ... and FOLLOW_HEIGHT units up ...
		   .add(targetEntityPosition);                         // ... from the player's position

		camera.position.set(tmp);
		float angle = MathHelpers.getYAngleBetween(targetEntityPosition, tmp) - 90.0f;
		camera.turnTo(angle);
		camera.pitchTo(FOLLOW_PITCH_ANGLE);
		camera.update();
	}
}
