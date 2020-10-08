package com.efsol.tagml.model;

public interface Document extends ChunkSequence {
    Layer getLayer(String layerName);

    Layer getGlobalLayer();

    default Object walkForwards(Layer layer, ChunkVisitor visitor) {
        return layer.walkForwards(visitor);
    }

    default Object walkBackwards(Layer layer, ChunkVisitor visitor) {
        return layer.walkForwards(visitor);
    }

    @Override
    default Object walkForwards(ChunkVisitor visitor) {
        return walkForwards(getGlobalLayer(), visitor);
    }

    @Override
    default Object walkBackwards(ChunkVisitor visitor) {
        return walkBackwards(getGlobalLayer(), visitor);
    }

    default Object walkLayerForwards(String layerName, ChunkVisitor visitor) {
        Layer layer = getLayer(layerName);
        if (null == layer) {
            throw new ModelException("attempt to walk unknown layer(" + layerName + ")");
        }
        return walkForwards(layer, visitor);
    }

    default Object walkLayerBackwards(String layerName, ChunkVisitor visitor) {
        Layer layer = getLayer(layerName);
        if (null == layer) {
            throw new ModelException("attempt to walk unknown layer(" + layerName + ")");
        }
        return walkBackwards(layer, visitor);
    }
}
