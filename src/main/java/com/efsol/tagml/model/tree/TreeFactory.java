package com.efsol.tagml.model.tree;

import java.util.Collection;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.parser.ParseContext;

public class TreeFactory implements DocumentFactory {
    public static boolean verbose = false;

    private Node start;

    public TreeFactory() {
        reset();
    }

    @Override
    public void reset() {
        start = null;
        // TODO
    }

    @Override
    public void addLayer(Layer layer) {
        // TODO
    }

    @Override
    public Chunk createChunk(String text, Position position, Map<String, Node> nodes) {
        // TODO
        return null;
    }

    @Override
    public Node createNode(String layerName, Collection<Tag> tags) {
        // TODO
        return null;
    }

    @Override
    public void addChunk(Chunk chunk) {
        // TODO
    }

    @Override
    public Document createDocument() {
        return new TreeDocument();
    }

    void log(String s) {
        if (verbose) {
            System.out.println("TreeFactory::" + s);
        }
    }

    @Override
    public ParseContext createContext() {
        // TODO Auto-generated method stub
        return null;
    }
}
