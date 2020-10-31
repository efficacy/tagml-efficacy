package com.efsol.tagml.model.dag;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;

import export.Export;
import export.dot.ColourSource;
import export.dot.Link;

class NodeFinder {
    private Map<String, String> index;
    private String dfl;

    public NodeFinder(String dfl) {
        this.index = new HashMap<>();
        this.dfl = dfl;
    }

    public void put(String key, String value) {
        index.put(key, value);
    }

    public String lookup(String key) {
        String p = index.get(key);
        if (null == p) {
            p = dfl;
            index.put(key, p);
        }
        return p;
    }

    public void reset() {
        index.clear();
    }
}

public class DotExport implements Export {
    private Document document;

    public DotExport(Document document) {
        this.document = document;
    }

    @Override
    public void export(OutputStream out, String comment) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        export(writer, comment);
        writer.flush();
    }

    public static String chunkId(Chunk chunk) {
        return String.valueOf(chunk.hashCode());
    }

    public void export(PrintWriter writer, String comment) throws IOException {
        ColourSource colours = new ColourSource();
        Map<String, String> layerColours = new HashMap<>();
        layerColours.put(Layer.GLOBAL_LAYER_NAME, "invis");
        layerColours.put(Layer.BASE_LAYER_NAME, "black");

        List<Chunk> chunks = new ArrayList<>();
        List<Link> links = new ArrayList<>();
        document.walkForwards(document.getGlobalLayer(), new ChunkVisitor() {
            @Override
            public Object visit(Chunk chunk) {
                chunks.add(chunk);
                return null;
            }
        });

        writer.println("digraph TextGraph {");
        writer.println(" start [shape=doublecircle;label=\"\"]");
        writer.println(" node [font=\"helvetica\";style=\"filled\";fillcolor=\"white\"]");
        writer.println(" subgraph {");

        NodeFinder prev = new NodeFinder("start");

        for (Chunk chunk : chunks) {
            String id = chunkId(chunk);
            links.add(new Link(prev.lookup(Layer.GLOBAL_LAYER_NAME), id, layerColours.get(Layer.GLOBAL_LAYER_NAME), null));
            writer.println("  " + id + " [shape=box, style=rounded;label=\"" + escape(chunk.getValue()) + "\"]");
            prev.put(Layer.GLOBAL_LAYER_NAME, id);
        }
        writer.println("  rank=same");
        for (Link link : links) {
            writer.println("  " + link.draw());
        }
        writer.println(" }");

        links.clear();
        prev.reset();

        for (Chunk chunk : chunks) {
            String id = chunkId(chunk);
            String boxId = "Box_" + id;
            StringBuilder nb = new StringBuilder();
            nb.append(" ");
            nb.append(boxId);
            nb.append(" [shape=box;labelloc=t;color=darkgray;label=<<table border='0' cellborder='0' cellspacing='0'>");
            int nlayers = 0;
            for (Node node : chunk.getLayers().values()) {
                String layerName = node.getLayerName();
                if (null == layerName) {
                    layerName = Layer.BASE_LAYER_NAME;
                }

                Collection<Tag> tags = node.getTags();
                if (!tags.isEmpty()) {
                    nb.append("<tr><td align='right'><b>");
                    nb.append(layerName);
                    nb.append("</b>:</td><td align='left'>");

                    boolean hadTag = false;
                    for (Tag tag : tags) {
                        if (hadTag) {
                            nb.append(", ");
                        }
                        nb.append(tag.name);
                        hadTag = true;
                    }
                    nb.append("</td></tr>");
                    ++nlayers;
                }

                if (!Layer.GLOBAL_LAYER_NAME.equals(layerName)) {
                    String colour = layerColours.get(layerName);
                    if (null == colour) {
                        colour = colours.nextColour();
                        layerColours.put(layerName, colour);
                    }
                    String p = prev.lookup(layerName);
                    links.add(new Link(p, id, colour, layerName));
                    prev.put(layerName, id);
                }
            }
            links.add(new Link(id, boxId, "darkgray", null, "arrowhead=none"));

            if (0 == nlayers) {
                nb.append("<tr><td>&nbsp;</td></tr>");
            }
            nb.append("</table>>]");
            writer.println(nb.toString());
        }
        for (Link link : links) {
            writer.println(" " + link.draw());
        }
        writer.println(" fontname=Courier;");
        writer.println(" label=\"" + escape(comment) + "\n(" + Layer.BASE_LAYER_NAME + "=Unnamed Layer)\";");
        writer.println("}");
    }

    private String escape(String comment) {
        return comment;
    }

}
