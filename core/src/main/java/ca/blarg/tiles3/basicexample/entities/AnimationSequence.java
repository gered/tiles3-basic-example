package ca.blarg.tiles3.basicexample.entities;

import com.badlogic.gdx.utils.Pool;

public class AnimationSequence implements Pool.Poolable {
	public int start;
	public int stop;
	public float delay;
	public boolean isMultiDirectional;
	public boolean hasDiagonalDirections;
	public int directionFrameOffset;

	public AnimationSequence set(int start, int stop, float delay) {
		this.start = start;
		this.stop = stop;
		this.delay = delay;
		isMultiDirectional = false;
		hasDiagonalDirections = false;
		directionFrameOffset = 0;
		return this;
	}

	public AnimationSequence set(int start, int stop, float delay, boolean isMultiDirectional, int directionFrameOffset) {
		this.start = start;
		this.stop = stop;
		this.delay = delay;
		this.isMultiDirectional = isMultiDirectional;
		this.hasDiagonalDirections = false;
		this.directionFrameOffset = directionFrameOffset;
		return this;
	}

	public AnimationSequence set(int start, int stop, float delay, boolean isMultiDirectional, boolean hasDiagonalDirections, int directionFrameOffset) {
		this.start = start;
		this.stop = stop;
		this.delay = delay;
		this.isMultiDirectional = isMultiDirectional;
		this.hasDiagonalDirections = hasDiagonalDirections;
		this.directionFrameOffset = directionFrameOffset;
		return this;
	}

	public AnimationSequence set(AnimationSequence other) {
		start = other.start;
		stop = other.stop;
		delay = other.delay;
		isMultiDirectional = other.isMultiDirectional;
		hasDiagonalDirections = other.hasDiagonalDirections;
		directionFrameOffset = other.directionFrameOffset;
		return this;
	}

	public void reset() {
		start = 0;
		stop = 0;
		delay = 0.0f;
		isMultiDirectional = false;
		hasDiagonalDirections = false;
		directionFrameOffset = 0;
	}
}
