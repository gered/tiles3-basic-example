package ca.blarg.tiles3.basicexample.entities.subsystems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.tiles3.basicexample.entities.FacingDirection;
import ca.blarg.tiles3.basicexample.entities.components.physics.OrientationXZComponent;
import ca.blarg.tiles3.basicexample.entities.components.rendering.AnimationComponent;
import ca.blarg.tiles3.basicexample.entities.components.rendering.AutoAnimationComponent;
import ca.blarg.tiles3.basicexample.entities.components.rendering.BillboardComponent;
import ca.blarg.tiles3.basicexample.entities.events.AnimationChangeEvent;
import ca.blarg.tiles3.basicexample.entities.events.AnimationFinishedEvent;

public class AnimationSystem extends ComponentSystem {
	final ViewportContext viewportContext;

	public AnimationSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
		viewportContext = Services.get(ViewportContext.class);

		listenFor(AnimationChangeEvent.class);
	}

	@Override
	public void dispose() {
		stopListeningFor(AnimationChangeEvent.class);
	}

	@Override
	public void onUpdateFrame(float delta) {
		updateAnimationSequences(delta);
		updateEntityRenderFrames();
	}

	@Override
	public boolean handle(Event e) {
		if (e instanceof AnimationChangeEvent)
			return handle((AnimationChangeEvent)e);

		return false;
	}

	private void updateAnimationSequences(float delta) {
		for (Entity entity : entityManager.getAllWith(AnimationComponent.class)) {
			AnimationComponent animation = entity.get(AnimationComponent.class);

			// only update active animation sequences
			if (!animation.isAnimationFinished()) {
				animation.frameTime += delta;
				animation.interpolation += delta / animation.currentSequence.delay;

				if (animation.frameTime >= animation.currentSequence.delay) {
					// move to the next frame
					animation.frameTime = 0.0f;
					animation.interpolation = 0.0f;

					++animation.currentFrame;
					if (animation.currentFrame > animation.currentSequence.stop) {
						animation.currentFrame = animation.currentSequence.start;
						if (!animation.isLooping) {
							animation.currentSequence.start = animation.currentSequence.stop;
							animation.currentFrame = animation.currentSequence.stop;
							animation.nextFrame = animation.currentFrame;
							animation.frameTime = animation.currentSequence.delay;

							continue;
						}
					}

					++animation.nextFrame;
					if (animation.nextFrame > animation.currentSequence.stop) {
						if (!animation.isLooping)
							animation.nextFrame = animation.currentSequence.stop;
						else
							animation.nextFrame = animation.currentSequence.start;
					}
				}
			} else {
				// animation has finished (it is not looping and has reached the end)
				if (!animation.hasAnimationFinishEventBeenTriggered) {
					// and we haven't yet raised an event to signal this
					AnimationFinishedEvent finishedEvent = eventManager.create(AnimationFinishedEvent.class);
					finishedEvent.entity = entity;
					eventManager.trigger(finishedEvent);

					animation.hasAnimationFinishEventBeenTriggered = true;
				}
			}
		}
	}

	private void updateEntityRenderFrames() {
		// update texture atlas frame indexes for tile-animation components with
		// multi-directional animations based on the current camera orientation
		for (Entity entity : entityManager.getAllWith(AnimationComponent.class)) {
			AnimationComponent animation = entity.get(AnimationComponent.class);
			BillboardComponent billboard = entity.get(BillboardComponent.class);
			if (billboard != null) {
				OrientationXZComponent orientation = entity.get(OrientationXZComponent.class);
				updateBillboardFrame(billboard, animation, orientation, viewportContext.getPerspectiveCamera());
			} else
				throw new UnsupportedOperationException("Unsupported animation method.");
		}
	}

	private boolean handle(AnimationChangeEvent e) {
		if (e.changeToSequenceForState) {
			// event is for an animation change to a sequence based on an entity
			// state provided in the event info. requires an AutoAnimationComponent
			AutoAnimationComponent autoAnimation = e.entity.get(AutoAnimationComponent.class);
			if (autoAnimation != null) {
				// if the entity's auto animation info has a sequence defined for
				// the state specified in the event, then switch to that sequence
				AutoAnimationComponent.AutoAnimationSequenceProperties sequenceProps = autoAnimation.mappings.get(e.state);
				if (sequenceProps != null) {
					AnimationComponent animation = e.entity.get(AnimationComponent.class);

					if (!animation.currentSequenceUnoverrideable ||
					    animation.isAnimationFinished() ||
					    e.overrideExisting)
						animation.setSequence(
								sequenceProps.name,
								sequenceProps.loop,
								sequenceProps.cannotBeOverridden
						);
				}
			}
		} else {
			// we're just changing to a named animation sequence directly
			AnimationComponent animation = e.entity.get(AnimationComponent.class);
			animation.setSequence(e.sequenceName, true, false);
		}

		return false;
	}

	static final Vector3 cameraPos = new Vector3();
	static final Vector3 cameraTarget = new Vector3();
	private void updateBillboardFrame(BillboardComponent billboard, AnimationComponent animation, OrientationXZComponent orientation, Camera camera) {
		if (billboard.atlas == null)
			return;

		if (orientation != null && animation.currentSequence.isMultiDirectional) {
			// get the direction to point the entity in based on it's own facing
			// direction and the direction the camera is pointed in
			cameraPos.set(camera.position);
			cameraTarget.set(camera.position)
			            .add(camera.direction);
			float yaw = MathHelpers.getYAngleBetween(cameraTarget, cameraPos);
			int direction = FacingDirection.getFacingDirectionAdjustedForCamera(orientation.angle, yaw, !animation.currentSequence.hasDiagonalDirections).value();

			// +1 because sequences will be specified using start/stop frames that
			// are a part of the sequence. e.g. for a 2 frame sequence using frames
			// 10 and 11, you would specify start=10 and stop=11, so without +1, the
			// below calc would find the length of this sequence to be 1 which is
			// not correct...
			int sequenceLength = animation.currentSequence.stop - animation.currentSequence.start + 1;

			// offset between frames for different directions
			int offset = direction * animation.currentSequence.directionFrameOffset;

			// set the frame index
			billboard.tileIndex = animation.currentFrame + (sequenceLength * direction) + offset;
		} else {
			billboard.tileIndex = animation.currentFrame;
		}
	}
}
