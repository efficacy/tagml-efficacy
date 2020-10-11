package com.efsol.tagml.model;

import java.util.Collection;
import java.util.Map;

public abstract class Chunk {
    protected final String value;
    protected final Position position;
    protected final Map<String, Node> nodes;

    public Chunk(String value, Position position, Map<String, Node> nodes) {
        this.value = value;
        this.position = position;
        this.nodes = nodes;
    }

    public String getValue() {
        return value;
    }

    public Position getPosition() {
        return position;
    }

    public Map<String, Node> getLayers() {
        return nodes;
    }

    public Node getLayerNode(String layerName) {
        return nodes.get(layerName);
    }

    public void addLayer(String layerName, Node node) {
        nodes.put(layerName, node);
    }

    public boolean isOnLayer(String layerName) {
        Node layer = nodes.get(layerName);
        if (null == layer)
            return false;
        Collection<Tag> tags = layer.getTags();
        if (null == tags)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Chunk(" + value + ")" + nodes + " at " + position;
    }

    public abstract Collection<Chunk> getNext(String layerName);
    public abstract Collection<Chunk> getPrevious(String layerName);
}
