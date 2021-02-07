package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.tilemap3d.Tile;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.lighting.LightSpreadingTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.LitChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;

public class Level {
	public static TileMap create() {
		ContentCache content = Services.get(ContentCache.class);

		TileMap map = new TileMap(
				16, 16, 16,
				2, 1, 2,
				content.tiles,
				new LitChunkVertexGenerator(),
				new LightSpreadingTileMapLighter());

		/*
		0  empty
		1  grass
		2  grass+dirt
		3  dirt
		4  leaves
		5  trunk
		6  planks
		7  stone brick
		8  stone floor tile
		9  sand
		*/

		for (int x = 0; x < map.getWidth(); ++x) {
			for (int z = 0; z < map.getDepth(); ++z) {
				if (x == 0 || z == 0 || x == map.getWidth() - 1 || z == map.getDepth() - 1) {
					map.get(x, 0, z).set(7, Tile.FLAG_COLLIDEABLE);
					map.get(x, 1, z).set(7, Tile.FLAG_COLLIDEABLE);
					map.get(x, 2, z).set(7, Tile.FLAG_COLLIDEABLE);
				} else
					map.get(x, 0, z).set(2, Tile.FLAG_COLLIDEABLE);

			}
		}

		for (int x = 10; x < map.getWidth() - 10; x += 7) {
			Prefabs.tree.placeIn(map, x, 1, 3, TilePrefab.Rotation.ROT0, false);
			Prefabs.tree.placeIn(map, x, 1, map.getDepth() - 3 - Prefabs.tree.getDepth(), TilePrefab.Rotation.ROT0, false);
		}

		for (int z = 10; z < map.getWidth() - 10; z += 7) {
			Prefabs.tree.placeIn(map, 3, 1, z, TilePrefab.Rotation.ROT0, false);
			Prefabs.tree.placeIn(map, map.getWidth() - 3 - Prefabs.tree.getWidth(), 1, z, TilePrefab.Rotation.ROT0, false);
		}

		Prefabs.tree.placeIn(map, 4, 1, 4, TilePrefab.Rotation.ROT0, false);
		Prefabs.tree.placeIn(map, map.getWidth() - 4 - Prefabs.tree.getWidth(), 1, 4, TilePrefab.Rotation.ROT0, false);
		Prefabs.tree.placeIn(map, 4, 1, map.getDepth() - 4 - Prefabs.tree.getDepth(), TilePrefab.Rotation.ROT0, false);
		Prefabs.tree.placeIn(map, map.getWidth() - 4 - Prefabs.tree.getWidth(), 1, map.getDepth() - 4 - Prefabs.tree.getDepth(), TilePrefab.Rotation.ROT0, false);

		map.updateLighting();
		map.updateVertices();

		return map;
	}
}
