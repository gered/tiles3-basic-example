package ca.blarg.tiles3.basicexample.entities.forces;

import ca.blarg.gdx.GameLooper;
import ca.blarg.gdx.Services;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.tiles3.basicexample.entities.PhysicsConstants;
import ca.blarg.tiles3.basicexample.entities.components.physics.PhysicsComponent;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class JumpForce extends Force {
	PhysicsComponent physicsComponent;

	public Force set(Vector3 direction, float strength, float friction, PhysicsComponent physicsComponent) {
		// we will almost certainly want a jump to last beyond 1 update tick
		float tickFrequency = Services.get(GameLooper.class).getUpdateFrequency();
		float minimumDuration = tickFrequency + 0.01f;

		super.set(direction, strength, friction)
		     .setMinimumDuration(minimumDuration)
		     .setZeroTolerance(PhysicsConstants.ON_GROUND_ZERO_TOLERANCE);
		this.physicsComponent = physicsComponent;
		return this;
	}

	@Override
	public void update(float delta) {
		super.update(delta);

		// Force.update() call could potentially kill this force and then return
		// here, so we should check for that first
		if (!isActive())
			return;

		boolean cancelled = false;

		// jumping should be cancelled when the top of the entity hits a surface
		// that is more or less perpendicular to the jumping direction (positive Y).
		// it should also be stopped if the entity has landed on some ground early
		// along in the jump (e.g. they jumped up to a ledge)
		if (physicsComponent.sweptSphere.foundCollision || physicsComponent.isSliding) {
			if (physicsComponent.isSliding || physicsComponent.slidingPlaneNormal.direction.y > 0.0f) {
				// if we're sliding, then check the angle
				float slideYAngle = (float)Math.acos(physicsComponent.sweptSphere.slidingPlaneNormal.dot(Vector3.Y)) * MathUtils.radiansToDegrees;

				// Y axis angle's from 135 to 225 means we hit something overhead.
				// 180 degrees = hit something perfectly perpendicular to the Y axis
				// HACK: also we check for a Y angle of zero as that will handle an
				// edge case where the entity jumped and immediately overhead there
				// is an obstacle (in this case, most of the time, the slide Y angle
				// is actually for the collision below their feet...)
				if ((slideYAngle > 135.0f && slideYAngle < 225.0f) || MathHelpers.areAlmostEqual(slideYAngle, 0.0f))
					cancelled = true;
			} else
				// not sliding, just a full-on collision with something
				// (collision but not sliding means it's usually a flat area of the
				// ground), so this is really IsOnGround() == TRUE (??)
				cancelled = true;
		}

		// don't kill it, even if there was a collision found above, if the force
		// hasn't been running for the minimum duration yet
		if (cancelled && hasMinDurationPassed())
			kill();
	}

	@Override
	public void reset() {
		super.reset();
		physicsComponent = null;
	}
}
