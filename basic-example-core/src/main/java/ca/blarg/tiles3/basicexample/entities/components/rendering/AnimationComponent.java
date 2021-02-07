package ca.blarg.tiles3.basicexample.entities.components.rendering;

import ca.blarg.gdx.entities.Component;
import ca.blarg.tiles3.basicexample.entities.AnimationSequence;
import ca.blarg.tiles3.basicexample.entities.AnimationSequenceCollection;

public class AnimationComponent extends Component {
	public final AnimationSequenceCollection sequences = new AnimationSequenceCollection();
	public int currentFrame;
	public int nextFrame;
	public float frameTime;
	public float interpolation;
	public boolean isLooping;
	public boolean currentSequenceUnoverrideable;
	public boolean hasAnimationFinishEventBeenTriggered;
	public String currentSequenceName;
	public final AnimationSequence currentSequence = new AnimationSequence();

	public boolean isAnimating() {
		return (currentSequence.start != currentSequence.stop);
	}

	public boolean isAnimationFinished() {
		// let the last frame of a non-looping animation (even a 1-frame
		// "animation") stay visible for at least the sequence's delay time as long
		// as it's non-zero
		if (!isLooping && currentFrame == currentSequence.stop && currentSequence.delay > 0.0f) {
			if (frameTime < currentSequence.delay)
				return false;   // still working through the last frame time
			else
				return true;

		}

		return (isAnimating() && !isLooping && currentFrame == currentSequence.stop);
	}

	public AnimationComponent setSequence(String name, boolean loop, boolean cannotBeOverridden) {
		AnimationSequence sequence = sequences.get(name);
		if (sequence == null)
			return this;

		currentSequence.set(sequence);
		currentSequenceName = name;

		currentFrame = currentSequence.start;
		nextFrame = currentFrame + 1;
		if (nextFrame > currentSequence.stop)
			nextFrame = currentSequence.stop;

		frameTime = 0.0f;
		interpolation = 0.0f;
		isLooping = loop;
		currentSequenceUnoverrideable = cannotBeOverridden;
		hasAnimationFinishEventBeenTriggered = false;
		return this;
	}

	public AnimationComponent stopSequence() {
		currentSequence.reset();
		currentFrame = 0;
		nextFrame = 0;
		frameTime = 0.0f;
		interpolation = 0.0f;
		isLooping = false;
		currentSequenceUnoverrideable = false;
		hasAnimationFinishEventBeenTriggered = false;
		return this;
	}

	@Override
	public void reset() {
		sequences.reset();
		currentFrame = 0;
		nextFrame = 0;
		frameTime = 0.0f;
		interpolation = 0.0f;
		isLooping = false;
		currentSequenceUnoverrideable = false;
		hasAnimationFinishEventBeenTriggered = false;
		currentSequenceName = null;
		currentSequence.reset();
	}
}
