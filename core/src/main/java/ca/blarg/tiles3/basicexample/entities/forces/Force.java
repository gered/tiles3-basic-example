package ca.blarg.tiles3.basicexample.entities.forces;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import ca.blarg.gdx.math.MathHelpers;

public class Force implements Pool.Poolable {
	boolean active;
	final Vector3 direction = new Vector3();
	float strength;
	float friction;
	final Vector3 currentForce = new Vector3();
	float duration;
	float minDuration;
	float zeroTolerance = MathHelpers.EPSILON;

	final Vector3 tempCurrentForce = new Vector3();

	public Force set(Vector3 direction, float strength, float friction) {
		active = true;
		this.direction.set(direction);
		this.strength = strength;
		this.friction = friction;
		currentForce.set(Vector3.Zero);
		duration = 0.0f;
		minDuration = 0.0f;
		return this;
	}

	public Force setMinimumDuration(float minDuration) {
		this.minDuration = minDuration;
		return this;
	}

	public Force setZeroTolerance(float tolerance) {
		this.zeroTolerance = tolerance;
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isZeroStrength() {
		return MathHelpers.areAlmostEqual(strength, 0.0f, zeroTolerance);
	}

	public boolean hasMinDurationPassed() {
		return duration >= minDuration;
	}

	public Vector3 getCurrentForce() {
		tempCurrentForce.set(currentForce);
		return tempCurrentForce;
	}

	public void kill() {
		active = false;
		currentForce.set(Vector3.Zero);
	}

	public void update(float delta) {
		if (!active)
			return;

		if (isZeroStrength()) {
			kill();
			return;
		}

		duration += delta;
		currentForce.set(direction).scl(strength);
		strength *= friction;
	}

	@Override
	public void reset() {
		active = false;
		direction.set(Vector3.Zero);
		strength = 0.0f;
		friction = 0.0f;
		currentForce.set(Vector3.Zero);
		duration = 0.0f;
		minDuration = 0.0f;
		zeroTolerance = MathHelpers.EPSILON;
	}
}
