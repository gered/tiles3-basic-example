package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.GameLooper;
import ca.blarg.tiles3.basicexample.entities.components.PlayerComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.PhysicsComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.PositionComponent;
import ca.blarg.tiles3.basicexample.entities.components.rendering.AnimationComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.ExtendedSpriteBatch;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.gdx.processes.GameProcess;
import ca.blarg.gdx.processes.ProcessManager;
import ca.blarg.tiles3.basicexample.entities.components.StateComponent;

public class DebugInfoProcess extends GameProcess {
	StringBuilder sb = new StringBuilder(1024);

	final ViewportContext viewportContext;
	final ExtendedSpriteBatch spriteBatch;
	final EntityManager entityManager;
	final ContentCache content;

	public DebugInfoProcess(ProcessManager processManager, EventManager eventManager) {
		super(processManager, eventManager);

		viewportContext = Services.get(ViewportContext.class);
		spriteBatch = Services.get(ExtendedSpriteBatch.class);
		entityManager = Services.get(EntityManager.class);
		content = Services.get(ContentCache.class);
	}

	@Override
	public void onRender(float interpolation) {
		sb.length = 0;

		GameLooper looper = Services.get(GameLooper.class);

		sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append(", UD: ").append(looper.getUpdateDelta())
		  .append(", PS: ").append(viewportContext.getOrthographicViewport().getUnitsPerPixel())
		  .append(" (").append((int)viewportContext.getOrthographicViewport().getWorldWidth()).append('x').append((int)viewportContext.getOrthographicViewport().getWorldHeight()).append(")").append('\n');
		sb.append("CP: ").append(viewportContext.getPerspectiveCamera().position.x).append(',')
		  .append(viewportContext.getPerspectiveCamera().position.y).append(',')
		  .append(viewportContext.getPerspectiveCamera().position.z).append('\n');

		if (entityManager != null) {
			Entity player = entityManager.getFirstWith(PlayerComponent.class);
			if (player != null)
				showEntityInfo(player);
		}

		spriteBatch.begin();
		spriteBatch.draw(content.font, 5, viewportContext.getOrthographicViewport().getWorldHeight() - 5, sb, viewportContext.getOrthographicViewport().getUnitsPerPixel());
		spriteBatch.end();
	}

	private void showEntityInfo(Entity entity) {
		PositionComponent position = entity.get(PositionComponent.class);
		StateComponent state = entity.get(StateComponent.class);
		AnimationComponent animation = entity.get(AnimationComponent.class);
		PhysicsComponent physics = entity.get(PhysicsComponent.class);

		sb.append("EP: ").append(position.position.x).append(',').append(position.position.y).append(',').append(position.position.z).append('\n');

		sb.append("EV: ").append(physics.velocity.x).append(',').append(physics.velocity.y).append(',').append(physics.velocity.z).append('\n');
		sb.append("C: ").append(physics.sweptSphere.foundCollision)
		  .append(" CD: ").append(physics.sweptSphere.nearestCollisionDistance)
		  .append(" S: ").append(physics.isSliding)
		  .append(" SA: ").append(MathUtils.radDeg * (float)Math.acos(physics.sweptSphere.slidingPlaneNormal.dot(Vector3.Y))).append('\n');
		sb.append("OG: ").append(physics.isOnGround)
		  .append(" W: ").append(physics.isWalking).append(" WW: ").append(physics.wasWalking)
		  .append(" M: ").append(physics.isInMotion).append(" WM: ").append(physics.wasInMotion)
		  .append(" F: ").append(physics.isFalling).append(" WF: ").append(physics.wasFalling).append('\n');
		sb.append("CP: ").append(physics.nearestIntersection.x).append(',')
		  .append(physics.nearestIntersection.y).append(',')
		  .append(physics.nearestIntersection.z).append('\n');

		sb.append("ES: ").append(state.state).append(" (").append(state.isInActionState).append(" - ").append(state.actionState).append(')').append('\n');

		sb.append("AS: ").append(animation.currentSequenceName)
		  .append(" (T: ").append(animation.frameTime).append(", CF: ").append(animation.currentFrame).append(", NF: ").append(animation.nextFrame)
		  .append(" (FS: ").append(animation.currentSequence.start).append(", FE: ").append(animation.currentSequence.stop).append(")")
		  .append(" L: ").append(animation.isLooping).append(", UO: ").append(animation.currentSequenceUnoverrideable).append(")")
		  .append(" A: ").append(animation.isAnimating()).append(", F: ").append(animation.isAnimationFinished()).append('\n');

	}
}
