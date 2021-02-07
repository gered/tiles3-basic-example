package ca.blarg.tiles3.basicexample.entities;

import ca.blarg.gdx.entities.Entity;
import ca.blarg.tiles3.basicexample.entities.components.physics.PositionComponent;
import com.badlogic.gdx.math.Vector3;

public class EntityUtils {
	public static void getInterpolatedPosition(Entity entity, float alpha, Vector3 outPosition) {
		PositionComponent position = entity.get(PositionComponent.class);
		outPosition.set(position.lastPosition);
		outPosition.lerp(position.position, alpha);
	}
}
