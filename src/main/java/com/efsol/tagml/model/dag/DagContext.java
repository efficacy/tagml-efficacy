package com.efsol.tagml.model.dag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.model.helper.LayerContext;
import com.efsol.tagml.parser.ParseContext;
import com.efsol.tagml.parser.ParseException;

public class DagContext implements ParseContext {
    public static boolean verbose = false;
    private DocumentFactory factory;
    private Map<String, LayerContext> layers;
    private StringBuilder buffer;
    private Position position;

    public DagContext(DocumentFactory factory) {
        this.factory = factory;
        this.layers = new HashMap<>();
        this.buffer = new StringBuilder();
        this.position = null;
    }

    @Override
    public Collection<LayerContext> getLayers() {
        return layers.values();
    }

    @Override
    public void addText(String text, Position position) {
        append(text);
        setPosition(position);
        factory.addChunk(swap());
    }

    @Override
    public void addTag(String name, Collection<String> layers, Position position) {
        log("addTag(" + name + "," + layers + ") at " + position);
        if (null == layers || layers.isEmpty()) {
            addTagToLayer(name, null, position);
        } else {
            for (String layer : layers) {
                addTagToLayer(name, layer, position);
            }
        }
    }

    private void addTagToLayer(String name, String layer, Position position) {
        LayerContext context = this.layers.get(layer);
        if (null == context) { // Auto-Create layers for now
            context = new LayerContext(layer);
            this.layers.put(layer, context);
//			throw new ParseException("attempt to add tag to unknown layer: " + layer, position);
        }
        Tag tag = new Tag(name, layer, position, null, null);
        context.add(tag);
    }

    @Override
    public void removeTag(String name, Collection<String> layers, Position position) throws ParseException {
        log("removeTag(" + name + "," + layers + ") at " + position);
        if (null == layers || layers.isEmpty()) {
            removeTagFromLayer(name, null, position);
        } else {
            for (String layer : layers) {
                removeTagFromLayer(name, layer, position);
            }
        }
    }

    private void removeTagFromLayer(String name, String layer, Position position) throws ParseException {
        LayerContext context = this.layers.get(layer);
        if (null == context) {
            throw new ParseException("attempt to close unopened tag " + name + " on layer: " + layer, position);
        }
        Tag tag = context.removeTag(name);
        if (null == tag) {
            log("removeTag(" + name + ") layer=" + layer + " pos=" + position);
            throw new ParseException("attempt to close unopened tag " + name + " on layer: " + layer, position);
        }
        if (context.isEmpty()) {
            this.layers.remove(layer);
        }
    }

    public void append(String text) {
        this.buffer.append(text);
    }

    public Chunk swap() {
        log("swap pos=" + position);
        Chunk ret = null;
        if (buffer.length() > 0) {
            String text = buffer.toString();
            log("swap:text=" + text);
            buffer.setLength(0);
            Map<String, Node> nodeLayers = new HashMap<>();
            for (String layerName : this.layers.keySet()) {
                log("swap: considering layer " + layerName);
                List<Tag> tags = new ArrayList<>();
                log("swap: got tags: " + tags);
                for (Tag tag : this.layers.get(layerName).getTags()) {
                    tags.add(tag);
                }
                log("swap: after adds tags: " + tags);
                Node node = factory.createNode(layerName, tags);
                nodeLayers.put(layerName, node);
            }
            ret = factory.createChunk(text, position, nodeLayers);
        }
        this.position = null;
        log("swap returning " + ret);
        return ret;
    }

    @Override
    public boolean isIncomplete() {
        return buffer.length() > 0;
    }

    @Override
    public void setPosition(Position position) {
        log("setPosition old=" + this.position + " new=" + position);
        if (null == this.position) {
            this.position = position;
        }
    }

    @Override
    public String toString() {
        return "ParseContext(" + buffer.toString() + ") " + layers + " at " + position;
    }

    void log(String s) {
        if (verbose)
            System.out.println("ParseContext::" + s);
    }

    @Override
    public void enforceConsistency() throws ParseException {
        List<Tag> unclosed = new ArrayList<>();
        for (String layerName : layers.keySet()) {
            LayerContext layer = layers.get(layerName);
            for (Tag tag : layer.getTags()) {
                unclosed.add(tag);
            }
        }
        if (!unclosed.isEmpty()) {
            StringBuilder error = new StringBuilder();
            boolean had = false;
            for (Tag tag : unclosed) {
                if (had) {
                    error.append(",");
                }
                error.append(tag.toOpenString());
            }
            throw new ParseException("unclosed tag(s): " + error, unclosed.get(0).position);
        }
    }

    @Override
    public void tail() {
        if (isIncomplete()) {
            log("at end, buffer is incomplete");
            Chunk chunk = swap();
            log("completed incomplete chunk: " + chunk);
            factory.addChunk(chunk);
        }
    }
}
