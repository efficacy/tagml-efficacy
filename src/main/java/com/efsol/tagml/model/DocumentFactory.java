package com.efsol.tagml.model;

public interface DocumentFactory extends ChunkFactory, NodeFactory {
	void addLayer(Layer layer);
	void addChunk(Chunk chunk);
	Document createDocument();
	void reset();
}
