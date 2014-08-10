package ca.blarg.tiles3.basicexample.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ca.blarg.gdx.GdxGameAppListener;
import ca.blarg.tiles3.basicexample.BasicExampleGameApp;

public class DesktopStarter {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Tiles³ Basic Example";
		cfg.width = 1280;
		cfg.height = 720;
		new LwjglApplication(new GdxGameAppListener(BasicExampleGameApp.class), cfg);
	}
}