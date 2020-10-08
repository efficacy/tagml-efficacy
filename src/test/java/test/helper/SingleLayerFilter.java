package test.helper;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.util.Utils;

public class SingleLayerFilter implements DocumentFilter {
    private final String layerName;

    public SingleLayerFilter(String layerName) {
        this.layerName = layerName;
    }

    @Override
    public boolean accept(Chunk chunk) {
        return chunk.isOnLayer(layerName);
    }

    @Override
    public boolean acceptLayer(String layer) {
        return Utils.same(layer, layerName);
    }
}