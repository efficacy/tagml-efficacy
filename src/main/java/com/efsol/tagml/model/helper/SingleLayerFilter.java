package com.efsol.tagml.model.helper;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.LayerPolicy;
import com.efsol.util.Utils;

public class SingleLayerFilter implements DocumentFilter {
    private final String layerName;
    private final LayerPolicy policy;

    public SingleLayerFilter(String layerName) {
        this.layerName = layerName;
        this.policy = new LayerPolicy() {
            @Override
            public boolean outputLayer(String layer) {
                return !Utils.same(layerName, layer);
            }

            @Override
            public boolean followLayer(String layer) {
                return Utils.same(layerName, layer);
            }
        };
    }

    @Override
    public boolean accept(Chunk chunk) {
        return chunk.isOnLayer(layerName);
    }

    @Override
    public LayerPolicy getlayerPolicy() {
        return policy;
    }
}