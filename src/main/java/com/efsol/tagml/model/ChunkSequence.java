package com.efsol.tagml.model;

public interface ChunkSequence extends Iterable<Chunk> {
    Object walkForwards(ChunkVisitor visitor);

    Object walkBackwards(ChunkVisitor visitor);

    public default Object walk(ChunkVisitor visitor) {
        return walkForwards(visitor);
    }
}
