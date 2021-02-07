package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.entities.systemcomponents.InactiveComponent;
import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.tiles3.basicexample.entities.events.*;
import ca.blarg.tiles3.basicexample.entities.EntityState;
import ca.blarg.tiles3.basicexample.entities.components.StateComponent;

public class EntityStateSystem extends ComponentSystem {
	public EntityStateSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
		listenFor(EntityStateChangeEvent.class);
		listenFor(EntityExitingTempStateEvent.class);
		listenFor(AnimationFinishedEvent.class);
		listenFor(DespawnedEvent.class);
	}

	@Override
	public void dispose() {
		stopListeningFor(EntityStateChangeEvent.class);
		stopListeningFor(EntityExitingTempStateEvent.class);
		stopListeningFor(AnimationFinishedEvent.class);
		stopListeningFor(DespawnedEvent.class);
	}

	@Override
	public boolean handle(Event e) {
		if (e instanceof EntityStateChangeEvent)
			return handle((EntityStateChangeEvent)e);

		else if (e instanceof EntityExitingTempStateEvent)
			return handle((EntityExitingTempStateEvent)e);

		else if (e instanceof AnimationFinishedEvent)
			return handle((AnimationFinishedEvent)e);

		else if (e instanceof DespawnedEvent)
			return handle((DespawnedEvent)e);

		return false;
	}

	private boolean handle(EntityStateChangeEvent e)
	{
		StateComponent state = e.entity.get(StateComponent.class);
		if (state != null) {
			if (e.isActionState) {
				// set action state first
				state.actionState = e.state;
				state.isInActionState = true;

				// HACK: this was mainly added to prevent the death animation from
				//       playing twice in a row in a somewhat rare scenario. however
				//       this does seem to be a sensible check regardless ...
				boolean needToChangeAnimation = state.state != state.actionState;

				// trigger state change event
				EntityInTempStateEvent actionStateEvent = eventManager.create(EntityInTempStateEvent.class);
				actionStateEvent.entity = e.entity;
				eventManager.trigger(actionStateEvent);

				if (needToChangeAnimation) {
					// trigger animation change
					AnimationChangeEvent animationChangeEvent = eventManager.create(AnimationChangeEvent.class);
					animationChangeEvent.entity = e.entity;
					animationChangeEvent.changeToSequenceForState = true;
					animationChangeEvent.state = e.state;
					eventManager.trigger(animationChangeEvent);
				}
			} else {
				// HACK: this will (hopefully?) stop entity's from doing there
				//       death animation and then popping up to idle before
				//       despawning. need to verify (very hard to reproduce!)
				if (state.state == EntityState.Dead && e.state == EntityState.Idle)
					return false;

				// HACK: this was mainly added to prevent the death animation from
				//       playing twice in a row in a somewhat rare scenario. however
				//       this does seem to be a sensible check regardless ...
				boolean needToChangeAnimation = state.state != state.actionState;

				// set non-action state
				state.state = e.state;

				if (e.clearExistingActionStateInfo) {
					state.actionState = EntityState.None;
					state.isInActionState = false;
				}

				if (needToChangeAnimation) {
					// trigger animation change
					AnimationChangeEvent animationChangeEvent = eventManager.create(AnimationChangeEvent.class);
					animationChangeEvent.entity = e.entity;
					animationChangeEvent.changeToSequenceForState = true;
					animationChangeEvent.state = e.state;

					// HACK: this ensures that the death animation will play
					animationChangeEvent.overrideExisting = e.state == EntityState.Dead;

					eventManager.trigger(animationChangeEvent);
				}
			}
		}

		return false;
	}

	private boolean handle(EntityExitingTempStateEvent e) {
		StateComponent state = e.entity.get(StateComponent.class);
		if (state != null) {
			// HACK: this will (hopefully?) stop entity's from doing their
			//       death animation and then popping up to idle before
			//       despawning. need to verify (very hard to reproduce!)
			if (state.state == EntityState.Dead)
				return false;

			// HACK: this was mainly added to prevent the death animation from
			//       playing twice in a row in a somewhat rare scenario. however
			//       this does seem to be a sensible check regardless ...
			boolean needToChangeAnimation = state.state != state.actionState;

			// clear action state info
			state.isInActionState = false;
			state.actionState = EntityState.None;

			if (needToChangeAnimation) {
				// trigger animation change
				AnimationChangeEvent animationChangeEvent = eventManager.create(AnimationChangeEvent.class);
				animationChangeEvent.entity = e.entity;
				animationChangeEvent.changeToSequenceForState = true;
				animationChangeEvent.state = state.state;
				eventManager.trigger(animationChangeEvent);
			}
		}

		return false;
	}

	private boolean handle(AnimationFinishedEvent e) {
		StateComponent state = e.entity.get(StateComponent.class);

		// TODO: this all assumes that an AnimationFinishedEvent triggered for
		//       an entity with a StateComponent marked as "in action state"
		//       should *always* trigger an EntityExitingTempStateEvent. Maybe
		//       this should be set up some other way with less assumptions?
		if (state != null && state.isInActionState) {
			EntityExitingTempStateEvent exitingTempStateEvent = eventManager.create(EntityExitingTempStateEvent.class);
			exitingTempStateEvent.entity = e.entity;
			eventManager.trigger(exitingTempStateEvent);
		}

		return false;
	}

	private boolean handle(DespawnedEvent e) {
		e.entity.add(InactiveComponent.class);
		return false;
	}
}
