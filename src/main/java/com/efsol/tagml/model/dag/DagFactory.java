package com.efsol.tagml.model.dag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.parser.ParseContext;
import com.efsol.util.Utils;

public class DagFactory implements DocumentFactory {
    public static boolean verbose = false;
    private Map<String, Layer> layers = new HashMap<>();
    private DagLayer global;
    private DagLayer dfl;

    public DagFactory() {
        reset();
    }

    @Override
    public void reset() {
        this.layers = new HashMap<>();
        this.global = new DagLayer(Layer.GLOBAL_LAYER_NAME, this);
        this.dfl = new DagLayer(null, this);
        layers.put(Layer.GLOBAL_LAYER_NAME, this.global);
        layers.put(null, this.dfl);
    }

    @Override
    public void addLayer(Layer layer) {
        if (null == layer) {
            throw new DagModelException("attempt to add null layer to document");
        }
        String layerName = layer.getName();
        if (layers.containsKey(layerName)) {
            throw new DagModelException("attempt to add duplicate layer(" + layerName + ") to document");
        }
        layers.put(layerName, layer);
    }

    @Override
    public Chunk createChunk(String text, Position position, Map<String, Node> nodes) {
        DagChunk ret = new DagChunk(text, position, nodes);
        return ret;
    }

    @Override
    public Node createNode(String layerName, Collection<Tag> tags) {
        log("create node on layer " + layerName + " with tags " + tags);
        DagLayer layer = (DagLayer) layers.get(layerName);
        if (null == layer) {
//			throw new DagModelException("attempt to create node on unknown layer(" + layerName +")");
            // auto-create layers
            layer = new DagLayer(layerName, this);
            log("createNode created new layer(" + layerName + ") " + layer);
            layers.put(layerName, layer);
        }

        DagNode node = new DagNode(layer, tags, null, null);
        log("Created new node " + node);
        return node;
    }

    @Override
    public void addChunk(Chunk chunk) {
        if (null == chunk) {
            throw new DagModelException("attempt to add null chunk");
        }
        log("add chunk " + chunk);
        Map<String, Node> nodes = chunk.getLayers();
        Collection<String> layerNames = nodes.keySet();
        for (String layerName : layerNames) {
            DagLayer layer = (DagLayer) layers.get(layerName);
            layer.add(chunk);
            log(" " + layer.getName() + Utils.describe(layer, false));
        }
        if (layerNames.isEmpty()) {
            dfl.add(chunk);
            log(" default" + Utils.describe(dfl, false));
        }
        global.add(chunk);
        log(" global" + Utils.describe(global, false));

    }

    @Override
    public Document createDocument() {
//		layers.put(Layer.GLOBAL_LAYER_NAME, global);
//		layers.put(null, dfl);
        return new DagDocument(layers);
    }

    void log(String s) {
        if (verbose)
            System.out.println("DagFactory::" + s);
    }

    @Override
    public ParseContext createContext() {
        return new DagContext(this);
    }
}
