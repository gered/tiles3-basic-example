package ca.blarg.tiles3.basicexample.entities;

import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.math.MathHelpers;

public enum FacingDirection {
	North(0),
	NorthEast(1),
	East(2),
	SouthEast(3),
	South(4),
	SouthWest(5),
	West(6),
	NorthWest(7);

	int value;

	private FacingDirection(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	/**
	 * the enum values and static angle constants are set such that:
	 *
	 * 1. east  = +x direction
	 *    west  = -x direction
	 *    north = -z direction
	 *    south = +z direction
	 *
	 * 2. enum.value() * MULTIPLIER = the corresponding static angle constant for
	 *    the enum's direction
	 *    (e.g. FacingDirection.East.value() * MULTIPLIER == FacingDirection.EAST_ANGLE)
	 */

	public static float MULTIPLIER = 45.0f;

	public static float NORTH_ANGLE = 0.0f;
	public static float NORTHEAST_ANGLE = 45.0f;
	public static float EAST_ANGLE = 90.0f;
	public static float SOUTHEAST_ANGLE = 135.0f;
	public static float SOUTH_ANGLE = 180.0f;
	public static float SOUTHWEST_ANGLE = 225.0f;
	public static float WEST_ANGLE = 270.0f;
	public static float NORTHWEST_ANGLE = 315.0f;

	public static FacingDirection getFacingDirection(float orientationY, boolean limitToQuadrantDirections) {
		final float QUADRANT_ANGLE_ADJUSTMENT = 45.0f;
		final float QUADRANT_DIRECTION_DIVISOR = 90.0f;
		final float OCTANT_ANGLE_ADJUSTMENT =  QUADRANT_ANGLE_ADJUSTMENT / 2.0f;
		final float OCTANT_DIRECTION_DIVISOR = 45.0f;

		float adjustment = limitToQuadrantDirections ? QUADRANT_ANGLE_ADJUSTMENT : OCTANT_ANGLE_ADJUSTMENT;
		float divisor = limitToQuadrantDirections ? QUADRANT_DIRECTION_DIVISOR : OCTANT_DIRECTION_DIVISOR;

		// add 45 degrees initially to kind of "rotate" the 360 degree circle
		// clockwise because 315 -> 360 is also part of the "east" quadrant along
		// with 0 -> 45.
		float adjusted = (orientationY) + adjustment;
		if (adjusted >= 360.0f)
			adjusted -= 360.0f;

		return FacingDirection.values()[(int)(adjusted / divisor)];
	}

	public static FacingDirection getFacingDirection(Vector3 orientation, boolean limitToQuadrantDirections) {
		return getFacingDirection(orientation.y, limitToQuadrantDirections);
	}

	public static FacingDirection getFacingDirectionAdjustedForCamera(float objectOrientationY, float cameraYaw, boolean limitToQuadrantDirections) {
		// HACK: this pretty much means I should probably just forget about 'adjusting' the angles by 90.0f and such
		//       and just use the "native" orientations...
		float hackyAdjustedObjectOrientationY = objectOrientationY + 180.0f;

		float difference = MathHelpers.rolloverClamp(hackyAdjustedObjectOrientationY - cameraYaw, 0.0f, 360.0f);
		return getFacingDirection(difference, limitToQuadrantDirections);
	}

	public static float getFacingAngle(FacingDirection direction) {
		return ((float)direction.ordinal()) * MULTIPLIER;
	}
}