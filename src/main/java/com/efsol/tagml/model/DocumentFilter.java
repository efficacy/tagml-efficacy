package com.efsol.tagml.model;

class AllInclusiveLayerPolicy implements LayerPolicy {

    @Override
    public boolean followLayer(String layerName) {
        return true;
    }

    @Override
    public boolean outputLayer(String layerName) {
        return true;
    }

}

public interface DocumentFilter {
    public static final LayerPolicy all = new AllInclusiveLayerPolicy();

    boolean accept(Chunk chunk);

    default LayerPolicy getlayerPolicy() {
        return all;
    }
}
