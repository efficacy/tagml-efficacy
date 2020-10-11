package com.efsol.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;

public class Utils {
    public static final boolean same(Object a, Object b) {
        if (null == a && null == b)
            return true;
        if (null == a || null == b)
            return false;
        return a.equals(b);
    }

    public static <T> Set<T> difference(final Set<T> setOne, final Set<T> setTwo) {
        Set<T> result = new LinkedHashSet<T>(setOne);
        result.removeIf(setTwo::contains);
        return result;
    }

    public static <T> T justPickOne(Collection<T> collection) {
        if (null == collection || collection.isEmpty()) {
            return null;
        }
        return collection.iterator().next(); // ugly hack
    }

    public static Object describe(Layer layer, boolean skipGlobal) {
        final StringBuilder ret = new StringBuilder();
        layer.walk(new ChunkVisitor() {
            @Override
            public Object visit(Chunk chunk) {
                ret.append(" -> '");
                ret.append(chunk.getValue());
                ret.append("'");
                Map<String, Node> nodes = chunk.getLayers();
                if (!nodes.isEmpty()) {
                    boolean hadNode = false;
                    ret.append("(");
                    for (String layer : nodes.keySet()) {
                        if (Layer.GLOBAL_LAYER_NAME.equals(layer)) {
                            continue;
                        }
                        if (hadNode) {
                            ret.append(",");
                        }
                        Node node = nodes.get(layer);
                        Collection<Tag> tags = node.getTags();
                        if (!tags.isEmpty()) {
                            ret.append(layer);
                            ret.append("=");
                            boolean hadTag = false;
                            for (Tag tag : tags) {
                                if (hadTag) {
                                    ret.append(",");
                                }
                                ret.append(tag.name);
                                hadTag = true;
                            }
                        }
                        hadNode = true;
                    }
                    ret.append(")");
                }
                return null;
            }
        });
        return ret.toString();
    }
}
