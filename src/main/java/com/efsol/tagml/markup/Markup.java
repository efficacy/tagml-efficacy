package com.efsol.tagml.markup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkCollector;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.FilterVisitor;
import com.efsol.tagml.model.LayerPolicy;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;
import com.efsol.util.Utils;

public class Markup {
    public static boolean verbose = false;

    public static String asOpen(Tag tag, LayerPolicy policy) {
        StringBuilder ret = new StringBuilder("[");
        ret.append(tag.name);
        if (null != tag.layer && policy.outputLayer(tag.layer)) {
            ret.append("|");
            ret.append(tag.layer);
        }
        // TODO render namespace and annotations
        ret.append(">");
        return ret.toString();
    }

    public static String asClose(Tag tag, LayerPolicy policy) {
        StringBuilder ret = new StringBuilder("<");
        ret.append(tag.name);
        if (null != tag.layer && policy.outputLayer(tag.layer)) {
            ret.append("|");
            ret.append(tag.layer);
        }
        // TODO render namespace
        ret.append("]");
        return ret.toString();
    }

    public static String spoolAsText(Document document, DocumentFilter filter) {
        StringBuilder ret = new StringBuilder();
        ChunkCollector collector = new ChunkCollector() {
            @Override
            public void collect(Chunk chunk) {
                ret.append(chunk.getValue());
            }
        };
        FilterVisitor visitor = new FilterVisitor(filter, collector);
        document.walk(visitor);
        return ret.toString();
    }

    private static Set<Tag> mergeTags(Chunk chunk, DocumentFilter filter) {
        Set<Tag> ret = new LinkedHashSet<>();
        Map<String, Node> layers = chunk.getLayers();
        for (String layer : layers.keySet()) {
            if (filter.getlayerPolicy().followLayer(layer)) {
                Node node = layers.get(layer);
                Collection<Tag> tags = node.getTags();
                log(" layer " + layer + " has " + tags);
                for (Tag tag : tags) {
                    ret.add(tag);
                }
            }
        }
        return ret;
    }

    private static <T> Collection<T> reverse(Collection<T> values) {
        List<T> ret = new ArrayList<>();
        for (T t : values) {
            ret.add(t);
        }
        Collections.reverse(ret);
        return ret;
    }

    public static String spoolAsMarkup(Document document, DocumentFilter filter) {
        StringBuilder ret = new StringBuilder();
        Set<Tag> runningContext = new LinkedHashSet<>();
        ChunkCollector collector = new ChunkCollector() {
            @Override
            public void collect(Chunk chunk) {
                log("collect chunk(" + chunk.getValue() + ") running" + runningContext);
                Set<Tag> newContext = mergeTags(chunk, filter);
                Set<Tag> removes = Utils.difference(runningContext, newContext);
                for (Tag tag : reverse(removes)) {
                    log(" closing " + tag);
                    ret.append(Markup.asClose(tag, filter.getlayerPolicy()));
                }
                Set<Tag> adds = Utils.difference(newContext, runningContext);
                for (Tag tag : adds) {
                    log(" opening " + tag);
                    ret.append(Markup.asOpen(tag, filter.getlayerPolicy()));
                }
                ret.append(chunk.getValue());
                runningContext.clear();
                runningContext.addAll(newContext);
            }
        };
        FilterVisitor visitor = new FilterVisitor(filter, collector);
        document.walk(visitor);
        for (Tag tag : reverse(runningContext)) {
            log("tail. closing " + tag);
            ret.append(Markup.asClose(tag, filter.getlayerPolicy()));
        }
        return ret.toString();
    }

    static void log(String s) {
        if (verbose)
            System.out.println("Parser::" + s);
    }
}
