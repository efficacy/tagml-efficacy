package com.efsol.tagml.model;

import java.util.Map;

public interface ChunkFactory {
	Chunk createChunk(String text, Position position, Map<String, Node> nodes);
}
