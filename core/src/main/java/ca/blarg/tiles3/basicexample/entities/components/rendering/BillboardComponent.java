package ca.blarg.tiles3.basicexample.entities.components.rendering;

import com.badlogic.gdx.graphics.Texture;
import ca.blarg.gdx.entities.Component;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;

public class BillboardComponent extends Component {
	public int tileIndex;
	public float width;
	public float height;
	public boolean isAxisAligned;
	public Texture texture;
	public TextureAtlas atlas;

	public BillboardComponent set(float width, float height, Texture texture, boolean isAxisAligned) {
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.isAxisAligned = isAxisAligned;
		this.atlas = null;
		return this;
	}

	public BillboardComponent set(float width, float height, TextureAtlas atlas, int tileIndex, boolean isAxisAligned) {
		this.width = width;
		this.height = height;
		this.atlas = atlas;
		this.tileIndex = tileIndex;
		this.isAxisAligned = isAxisAligned;
		this.texture = null;
		return this;
	}

	@Override
	public void reset() {
		tileIndex = 0;
		width = 0.0f;
		height = 0.0f;
		isAxisAligned = false;
		texture = null;
		atlas = null;
	}
}
