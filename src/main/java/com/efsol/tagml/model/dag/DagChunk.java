package com.efsol.tagml.model.dag;

import java.util.Collection;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;

public class DagChunk extends Chunk {
    public DagChunk(String value, Position position, Map<String, Node> nodes) {
        super(value, position, nodes);
    }

    @Override
    public Collection<Chunk> getNext(String layerName) {
        DagNode node = (DagNode) nodes.get(layerName);
        if (null == node) {
            throw new DagModelException("failed to get next chunk on unknown layer(" + layerName + ") after " + getValue());
        }
        return node.next;
    }

    @Override
    public Collection<Chunk> getPrevious(String layerName) {
        DagNode node = (DagNode) nodes.get(layerName);
        if (null == node) {
            throw new DagModelException("failed to get previous chunk on unknown layer(" + layerName + ") after " + getValue());
        }
        return node.prev;
    }

    @Override
    public String toString() {
        return "DAGChunk(" + getValue() + ") nodes=" + nodes;
    }

}
