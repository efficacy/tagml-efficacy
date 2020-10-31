package com.efsol.tagml.model;

import com.efsol.tagml.parser.ParseContext;

public interface DocumentFactory extends ChunkFactory, NodeFactory {
    ParseContext createContext();
    void addLayer(Layer layer);
    void addChunk(Chunk chunk);
    Document createDocument();
    void reset();
}
