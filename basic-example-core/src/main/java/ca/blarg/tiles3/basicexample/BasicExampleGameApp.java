package ca.blarg.tiles3.basicexample;

import ca.blarg.gdx.GameApp;
import ca.blarg.gdx.Services;
import ca.blarg.gdx.tilemap3d.assets.TileAssetUtils;
import ca.blarg.gdx.tilemap3d.tilemesh.CollisionShapes;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;

public class BasicExampleGameApp extends GameApp {
	public BasicExampleGameApp() {
		toggleHeapMemUsageLogging(false);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Override
	public void onCreate() {
		TileAssetUtils.registerLoaders(Services.get(AssetManager.class));
		CollisionShapes.init();

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		ContentCache contentCache = new ContentCache();

		Services.register(this);
		Services.register(contentCache);
		Services.register(inputMultiplexer);

		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		stateManager.push(GamePlayState.class);
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);

		Services.unregisterAll();

		super.dispose();
	}
}
