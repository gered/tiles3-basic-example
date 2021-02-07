package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.tilemap3d.Tile;
import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;

public class Prefabs {
	public static final TilePrefab tree;

	static {
		tree = new TilePrefab(5, 6, 5);

		for (int x = 0; x < 5; ++x)
			for (int z = 0; z < 5; ++z)
				tree.get(x, 2, z).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(0, 2, 0).set(0, Tile.FLAG_COLLIDEABLE);
		tree.get(4, 2, 0).set(0, Tile.FLAG_COLLIDEABLE);
		tree.get(0, 2, 4).set(0, Tile.FLAG_COLLIDEABLE);
		tree.get(4, 2, 4).set(0, Tile.FLAG_COLLIDEABLE);

		for (int x = 1; x < 4; ++x)
			for (int z = 1; z < 4; ++z)
				tree.get(x, 3, z).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(0, 3, 2).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(4, 3, 2).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 3, 0).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 3, 4).set(4, Tile.FLAG_COLLIDEABLE);

		tree.get(2, 4, 2).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(1, 4, 2).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(3, 4, 2).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 4, 1).set(4, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 4, 3).set(4, Tile.FLAG_COLLIDEABLE);

		tree.get(2, 0, 2).set(5, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 1, 2).set(5, Tile.FLAG_COLLIDEABLE);
		tree.get(2, 2, 2).set(5, Tile.FLAG_COLLIDEABLE);
	}
}
