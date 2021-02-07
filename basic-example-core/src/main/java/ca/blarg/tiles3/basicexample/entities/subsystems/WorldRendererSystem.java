package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.tiles3.basicexample.entities.components.WorldComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.PositionComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.GraphicsHelpers;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.gdx.math.MathHelpers;
import ca.blarg.gdx.tilemap3d.TileMapRenderer;

public class WorldRendererSystem extends ComponentSystem {
	final static Color AMBIENT_LIGHT = new Color(0.7f, 0.7f, 0.7f, 1.0f);
	final static Color DIRECTIONAL_LIGHT = new Color(0.3f, 0.3f, 0.3f, 1.0f);
	final static Vector3 renderPosition = new Vector3();

	final TileMapRenderer tileMapRenderer;
	final Environment environment;

	final ViewportContext viewportContext;
	final ModelBatch modelBatch;
	final ShapeRenderer shapeRenderer;

	public WorldRendererSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);

		viewportContext = Services.get(ViewportContext.class);
		modelBatch = Services.get(ModelBatch.class);
		shapeRenderer = Services.get(ShapeRenderer.class);

		tileMapRenderer = new TileMapRenderer();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, AMBIENT_LIGHT));
		environment.add(new DirectionalLight().set(DIRECTIONAL_LIGHT, MathHelpers.LEFT_VECTOR3));
		environment.add(new DirectionalLight().set(DIRECTIONAL_LIGHT, MathHelpers.RIGHT_VECTOR3));
		environment.add(new DirectionalLight().set(DIRECTIONAL_LIGHT, MathHelpers.DOWN_VECTOR3));
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onRender(float interpolation) {
		Entity worldEntity = entityManager.getFirstWith(WorldComponent.class);
		if (worldEntity == null)
			return;

		WorldComponent world = worldEntity.get(WorldComponent.class);
		PositionComponent worldPosition = worldEntity.get(PositionComponent.class);
		if (worldPosition != null)
			renderPosition.set(worldPosition.position);
		else
			renderPosition.set(Vector3.Zero);

		if (world.renderSkybox) {
			// TODO
		}

		if (world.renderGrid)
			GraphicsHelpers.renderGridPlane(shapeRenderer, viewportContext.getPerspectiveCamera(),
			                                world.tileMap.getWidth(), world.tileMap.getDepth(),
			                                renderPosition.x, renderPosition.y, renderPosition.z);

		modelBatch.begin(viewportContext.getPerspectiveCamera());
		tileMapRenderer.render(modelBatch, world.tileMap, viewportContext.getPerspectiveCamera(), environment);
		modelBatch.end();
	}
}
