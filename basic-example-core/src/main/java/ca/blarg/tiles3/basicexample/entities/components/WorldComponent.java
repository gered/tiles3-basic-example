package ca.blarg.tiles3.basicexample.entities.components;

import ca.blarg.gdx.entities.Component;
import ca.blarg.gdx.tilemap3d.TileMap;

public class WorldComponent extends Component {
	public TileMap tileMap;
	public boolean renderGrid;
	public boolean renderSkybox;

	@Override
	public void reset() {
		tileMap = null;
		renderGrid = false;
		renderSkybox = false;
	}
}
