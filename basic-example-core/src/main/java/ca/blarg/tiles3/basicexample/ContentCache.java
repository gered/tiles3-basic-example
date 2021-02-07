package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.graphics.atlas.TextureAtlasAnimator;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ContentCache implements Services.Service {
	public BitmapFont font;

	public TileMeshCollection tiles;
	public TextureAtlas terrain;
	public TextureAtlasAnimator terrainAnimator = new TextureAtlasAnimator();

	public TextureAtlas player;

	@Override
	public void onRegister() {
		AssetManager assetManager = Services.get(AssetManager.class);

		assetManager.load("consoleFont.fnt", BitmapFont.class);

		assetManager.load("tiles/terrain.atlas.json", TextureAtlas.class);
		assetManager.load("player.atlas.json", TextureAtlas.class);
		assetManager.load("tiles/tiles.json", TileMeshCollection.class);

		// normally we would probably want to load asynchronously and show a loading bar of sorts
		assetManager.finishLoading();

		font = assetManager.get("consoleFont.fnt");
		terrain = assetManager.get("tiles/terrain.atlas.json");
		tiles = assetManager.get("tiles/tiles.json");
		player = assetManager.get("player.atlas.json");

		terrainAnimator.addAllAtlases(assetManager);
	}

	@Override
	public void onUnregister() {
	}
}
