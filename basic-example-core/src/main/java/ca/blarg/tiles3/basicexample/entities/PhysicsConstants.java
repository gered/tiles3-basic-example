package ca.blarg.tiles3.basicexample.entities;

import com.badlogic.gdx.math.Vector3;

public final class PhysicsConstants {
	public static final float UNITS_PER_METRE = 1.0f;

	public static final float GRAVITY_SPEED = 9.8f * UNITS_PER_METRE;
	public static final Vector3 GRAVITY = new Vector3(0.0f, -GRAVITY_SPEED, 0.0f);

	public static final float FRICTION_NORMAL = 0.5f;

	public static final float SLOPE_STEEP_Y_ANGLE = 46;
	public static final float ON_GROUND_ZERO_TOLERANCE = 0.1f;
}