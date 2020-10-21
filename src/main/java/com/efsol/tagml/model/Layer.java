package com.efsol.tagml.model;

public interface Layer extends ChunkSequence {
    String GLOBAL_LAYER_NAME = "$";
    String BASE_LAYER_NAME = "@";

    String getName();
}
