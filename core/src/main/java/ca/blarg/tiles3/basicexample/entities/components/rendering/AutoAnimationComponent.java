package ca.blarg.tiles3.basicexample.entities.components.rendering;

import com.badlogic.gdx.utils.ObjectMap;
import ca.blarg.gdx.entities.Component;
import ca.blarg.tiles3.basicexample.entities.EntityState;

public class AutoAnimationComponent extends Component {
	public class AutoAnimationSequenceProperties {
		public String name;
		public boolean loop;
		public boolean cannotBeOverridden;
	}

	public final ObjectMap<EntityState, AutoAnimationSequenceProperties> mappings = new ObjectMap<EntityState, AutoAnimationSequenceProperties>();

	public AutoAnimationComponent set(EntityState state, String name, boolean loop, boolean cannotBeOverridden) {
		AutoAnimationSequenceProperties props = new AutoAnimationSequenceProperties();
		props.name = name;
		props.loop = loop;
		props.cannotBeOverridden = cannotBeOverridden;
		mappings.put(state, props);
		return this;
	}

	@Override
	public void reset() {
		mappings.clear();
	}
}
