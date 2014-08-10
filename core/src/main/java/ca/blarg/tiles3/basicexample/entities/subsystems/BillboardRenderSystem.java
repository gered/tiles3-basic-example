package ca.blarg.tiles3.basicexample.entities.subsystems;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.entities.ComponentSystem;
import ca.blarg.gdx.entities.Entity;
import ca.blarg.gdx.entities.EntityManager;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.BillboardSpriteBatch;
import ca.blarg.gdx.graphics.ViewportContext;
import ca.blarg.tiles3.basicexample.entities.EntityUtils;
import ca.blarg.tiles3.basicexample.entities.components.physics.BoundingSphereComponent;
import ca.blarg.tiles3.basicexample.entities.components.physics.PositionComponent;
import ca.blarg.tiles3.basicexample.entities.components.rendering.BillboardComponent;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;

public class BillboardRenderSystem extends ComponentSystem {
	private static final float Y_COORD_OFFSET = -1.0f;
	private final Vector3 renderPosition = new Vector3();

	final ViewportContext viewportContext;
	final BillboardSpriteBatch billboardSpriteBatch;

	public BillboardRenderSystem(EntityManager entityManager, EventManager eventManager) {
		super(entityManager, eventManager);
		viewportContext = Services.get(ViewportContext.class);
		billboardSpriteBatch = Services.get(BillboardSpriteBatch.class);
	}

	@Override
	public void onRender(float interpolation) {
		Camera camera = viewportContext.getPerspectiveCamera();
		Frustum frustum = camera.frustum;

		billboardSpriteBatch.begin(camera);

		for (Entity entity : entityManager.getAllWith(BillboardComponent.class)) {
			BillboardComponent billboard = entity.get(BillboardComponent.class);
			PositionComponent position = entity.get(PositionComponent.class);
			BoundingSphereComponent bounds = entity.get(BoundingSphereComponent.class);

			if (bounds != null) {
				if (!frustum.sphereInFrustum(bounds.bounds.center, bounds.bounds.radius))
					continue;
			} else {
				if (!frustum.pointInFrustum(position.lastPosition))
					continue;
			}

			EntityUtils.getInterpolatedPosition(entity, interpolation, renderPosition);

			BillboardSpriteBatch.Type billboardType =
					(billboard.isAxisAligned ? BillboardSpriteBatch.Type.ScreenAligned :
					 BillboardSpriteBatch.Type.Spherical);

			float difference = (billboard.height / 2.0f) + Y_COORD_OFFSET;
			if (difference > 0.0f)
				renderPosition.y += difference;
			renderPosition.x += 0.35f;

			if (billboard.texture != null)
				billboardSpriteBatch.draw(
						billboardType, billboard.texture,
						renderPosition.x, renderPosition.y, renderPosition.z,
						billboard.width, billboard.height
				);
			else
				billboardSpriteBatch.draw(
						billboardType, billboard.atlas.get(billboard.tileIndex),
						renderPosition.x, renderPosition.y, renderPosition.z,
						billboard.width, billboard.height
				);
		}

		billboardSpriteBatch.end();
	}
}
