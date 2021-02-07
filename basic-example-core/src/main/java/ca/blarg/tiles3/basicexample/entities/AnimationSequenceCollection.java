package ca.blarg.tiles3.basicexample.entities;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class AnimationSequenceCollection implements Pool.Poolable {
	final ObjectMap<String, AnimationSequence> sequences = new ObjectMap<String, AnimationSequence>();

	public AnimationSequenceCollection() {
	}

	public AnimationSequence add(String key) {
		if (sequences.containsKey(key))
			throw new UnsupportedOperationException("Sequence with that name present in collection already");
		AnimationSequence sequence = Pools.obtain(AnimationSequence.class);
		sequences.put(key, sequence);
		return sequence;
	}

	public AnimationSequence get(String key) {
		return sequences.get(key);
	}

	@Override
	public void reset() {
		for (ObjectMap.Entry<String, AnimationSequence> i : sequences.entries())
			Pools.free(i.value);
		sequences.clear();
	}
}
