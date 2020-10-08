package com.efsol.tagml.model.dag;

import java.util.Iterator;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.Layer;
import com.efsol.util.Utils;

public class DagDocument implements Document {
    private final Map<String, Layer> layers;

    public DagDocument(Map<String, Layer> layers) {
        this.layers = layers;
    }

    @Override
    public Object walkLayerForwards(String layerName, ChunkVisitor visitor) {
        Layer layer = layers.get(layerName);
        if (null == layer) {
            throw new DagModelException("attempt to walk unknown layer(" + layerName + ")");
        }
        return layer.walkForwards(visitor);
    }

    @Override
    public Object walkLayerBackwards(String layerName, ChunkVisitor visitor) {
        Layer layer = layers.get(layerName);
        if (null == layer) {
            throw new DagModelException("attempt to walk unknown layer(" + layerName + ")");
        }
        return layer.walkForwards(visitor);
    }

    @Override
    public Layer getLayer(String layerName) {
        return layers.get(layerName);
    }

    @Override
    public Layer getGlobalLayer() {
        return layers.get(Layer.GLOBAL_LAYER_NAME);
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder("DAGDoc layers:\n");
        for (String key : layers.keySet()) {
            ret.append(" ");
            ret.append(key);
            ret.append(": ");
            ret.append(Utils.describe(layers.get(key), true));
            ret.append("\n");
        }
        return ret.toString();
    }

    @Override
    public Iterator<Chunk> iterator() {
        return getGlobalLayer().iterator();
    }
}
