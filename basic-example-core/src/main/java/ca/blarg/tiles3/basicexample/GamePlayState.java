package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.EulerPerspectiveCamera;
import ca.blarg.gdx.graphics.GraphicsHelpers;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.gdx.graphics.screeneffects.DimScreenEffect;
import ca.blarg.gdx.graphics.screeneffects.ScreenEffectHelpers;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.gdx.states.GameState;
import ca.blarg.gdx.states.StateManager;
import ca.blarg.tiles3.basicexample.entities.components.PlayerComponent;
import ca.blarg.tiles3.basicexample.entities.components.WorldComponent;
import ca.blarg.tiles3.basicexample.entities.events.JumpEvent;
import ca.blarg.tiles3.basicexample.entities.events.MoveAndTurnEvent;
import ca.blarg.tiles3.basicexample.entities.presets.PlayerPreset;
import ca.blarg.tiles3.basicexample.entities.subsystems.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;

public class GamePlayState extends GameState {
	final ViewportContext viewportContext;
	final ModelBatch modelBatch;
	final ContentCache content;

	EulerPerspectiveCamera camera;
	EntityManager entityManager;

	public GamePlayState(StateManager stateManager, EventManager eventManager) {
		super(stateManager, eventManager);
		viewportContext = Services.get(ViewportContext.class);
		modelBatch = Services.get(ModelBatch.class);
		content = Services.get(ContentCache.class);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void onPush() {
		camera = new EulerPerspectiveCamera(60.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.moveTo(12.0f, 10.0f, 16.0f);
		camera.pitch(35.0f);
		gameApp.viewportContext.setPerspectiveCamera(camera);

		entityManager = new EntityManager(eventManager);
		Services.register(entityManager);

		initEntitySystem();

		// world entity
		Entity worldEntity = entityManager.add();
		WorldComponent world = worldEntity.add(WorldComponent.class);
		world.tileMap = Level.create();
		world.renderGrid = false;
		world.renderSkybox = false;

		entityManager.addUsingPreset(PlayerPreset.class);

		processManager.add(DebugInfoProcess.class);
	}

	@Override
	public void onPop() {
		viewportContext.setDefaultPerspectiveCamera();
		Services.unregister(EntityManager.class);
		entityManager.dispose();
	}

	@Override
	public void onRender(float interpolation) {
		GraphicsHelpers.clear(0.0f, 0.0f, 0.0f, 1.0f);

		entityManager.onRender(interpolation);
		super.onRender(interpolation);
	}

	final static Vector2 moveDirection = new Vector2();
	@Override
	public void onUpdateGameState(float delta) {
		super.onUpdateGameState(delta);
		if (isTransitioning() || !isTopState())
			return;

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyPressed(Input.Keys.BACK))
			setFinished();

		Entity player = entityManager.getFirstWith(PlayerComponent.class);

		if (player != null) {
			float referenceAngle = ((EulerPerspectiveCamera)gameApp.viewportContext.getPerspectiveCamera()).getYaw();

			moveDirection.set(Vector2.Zero);
			if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
				moveDirection.y -= 1.0f;
			if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
				moveDirection.y += 1.0f;
			if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
				moveDirection.x -= 1.0f;
			if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				moveDirection.x += 1.0f;

			if (moveDirection.len2() != 0.0f) {
				moveDirection.nor();
				moveDirection.rotate(referenceAngle);

				MoveAndTurnEvent moveEvent = eventManager.create(MoveAndTurnEvent.class);
				moveEvent.entity = player;
				moveEvent.angle = MathHelpers.getAngleFromPointOnCircle(moveDirection.x, moveDirection.y);
				eventManager.queue(moveEvent);
			}

			if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
				JumpEvent jumpEvent = eventManager.create(JumpEvent.class);
				jumpEvent.entity = player;
				eventManager.queue(jumpEvent);
			}
		}

		entityManager.onUpdateGameState(delta);
	}

	@Override
	public void onUpdateFrame(float delta) {
		super.onUpdateFrame(delta);
		if (isTransitioning() || !isTopState())
			return;

		content.terrainAnimator.onUpdate(delta);
		entityManager.onUpdateFrame(delta);
	}

	@Override
	public void onAppPause() {
		super.onAppPause();
		entityManager.onAppPause();
	}

	@Override
	public void onAppResume() {
		super.onAppResume();
		entityManager.onAppResume();
	}

	@Override
	public void onPause(boolean dueToOverlay) {
		super.onPause(dueToOverlay);
		if (dueToOverlay)
			effectManager.add(DimScreenEffect.class);
	}

	@Override
	public void onResume(boolean fromOverlay) {
		super.onResume(fromOverlay);
		if (fromOverlay)
			effectManager.remove(DimScreenEffect.class);
	}

	@Override
	public boolean onTransition(float delta, boolean isTransitioningOut, boolean started) {
		return ScreenEffectHelpers.doFadingTransition(effectManager, isTransitioningOut, started);
	}

	private void initEntitySystem() {
		entityManager.addSubsystem(PreviousPositionSystem.class);
		entityManager.addSubsystem(PhysicsSystem.class);
		entityManager.addSubsystem(BoundingVolumeWorldPositioningSystem.class);
		entityManager.addSubsystem(EntityStateSystem.class);
		entityManager.addSubsystem(CameraSystem.class);
		entityManager.addSubsystem(AnimationSystem.class);
		entityManager.addSubsystem(WorldRendererSystem.class);
		entityManager.addSubsystem(BillboardRenderSystem.class);

		entityManager.addPreset(PlayerPreset.class);
	}
}
