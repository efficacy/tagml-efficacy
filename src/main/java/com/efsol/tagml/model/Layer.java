package com.efsol.tagml.model;

public interface Layer extends Iterable<Chunk> {
	String GLOBAL_LAYER_NAME = "$";

	String getName();
	Object walkForwards(ChunkVisitor visitor);
	Object walkBackwards(ChunkVisitor visitor);

	public default Object walk(ChunkVisitor visitor) {
		return walkForwards(visitor);
	}
}
