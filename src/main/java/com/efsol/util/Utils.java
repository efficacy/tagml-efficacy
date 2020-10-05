package com.efsol.util;

import java.util.HashSet;
import java.util.Set;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Layer;

public class Utils {
	public static final boolean same(Object a, Object b) {
		if (null == a && null == b) return true;
		if (null == a || null == b) return false;
		return a.equals(b);
	}

	public static <T> Set<T> difference(final Set<T> setOne, final Set<T> setTwo) {
	     Set<T> result = new HashSet<T>(setOne);
	     result.removeIf(setTwo::contains);
	     return result;
	}

	public static Object describe(Layer layer) {
		final StringBuilder ret = new StringBuilder();
		layer.walk(new ChunkVisitor() {
			@Override
			public Object visit(Chunk chunk) {
				ret.append("->");
				ret.append(chunk.getValue());
				return null;
			}
		});
		return ret.toString();
	}
}
