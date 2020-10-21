package export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;

class Link {
    public final String from;
    public final String to;
    public final String colour;
    public final String label;

    public Link(String from, String to, String colour, String label) {
        this.from = from;
        this.to = to;
        this.colour = colour;
        this.label = label;
    }

    public Link(String from, String to) {
        this(from, to, null, null);
    }

    public String draw() {
        StringBuilder ret = new StringBuilder();
        ret.append(from);
        ret.append("->");
        ret.append(to);
        if (null != colour || null != label) {
            boolean had = false;
            ret.append(" [");
            if (null != colour) {
                if (had) {
                    ret.append(";");
                }
                ret.append("color=");
                ret.append(colour);
                had = true;
            }
            if (null != label) {
                if (had) {
                    ret.append(";");
                }
                ret.append("label=\"");
                ret.append(label);
                ret.append("\"");
                had = true;
            }
            ret.append("]");
        }
        return ret.toString();
    }
}

class ColourSource {
    private String[] colours = { "red", "orange", "yellow", "green", "blue" };
    private int next = 0;

    public String nextColour() {
        return colours[next++];
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
        layerColours.put(Layer.GLOBAL_LAYER_NAME, "black");
        layerColours.put(Layer.BASE_LAYER_NAME, "gray");

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
        String prev = "start";
        links.add(new Link(prev, chunkId(chunks.get(0)), layerColours.get(Layer.BASE_LAYER_NAME), Layer.BASE_LAYER_NAME));
        for (Chunk chunk : chunks) {
            String id = chunkId(chunk);
            links.add(new Link(prev, id, "black", Layer.GLOBAL_LAYER_NAME));
            writer.println("  " + id + " [shape=box, style=rounded;label=\"" + escape(chunk.getValue()) + "\"]");
            prev = id;
        }
        writer.println("  rank=same");
        for (Link link : links) {
            writer.println("  " + link.draw());
        }
        writer.println(" }");
        links.clear();

        for (Chunk chunk : chunks) {
            String id = chunkId(chunk);
            for (Node node : chunk.getLayers().values()) {
                String layerName = node.getLayerName();
                if (null == layerName) {
                    layerName = Layer.BASE_LAYER_NAME;
                }
                if (!Layer.GLOBAL_LAYER_NAME.equals(layerName)) {
                    String colour = layerColours.get(layerName);
                    if (null == colour) {
                        colour = colours.nextColour();
                        layerColours.put(layerName, colour);
                    }
                    Chunk next = node.getNext();
                    if (null != next) {
                        String nextId = chunkId(next);
                        links.add(new Link(id, nextId, colour, layerName));
                    }
                }
            }
//            String chunkId = String.valueOf(chunk.hashCode());
//            if (null == firstId) {
//                firstId = chunkId;
//            }
//            writer.println("  subgraph {");
//            writer.println("   subgraph cluster" + chunkId + " {");
//            writer.println("    " + chunkId + " [shape=box;label=<#PCDATA<br/>" + chunk.getValue() + ">]");
//            String firstNode = null;
//                String nodeId = String.valueOf(node.hashCode());
//                if (null == firstNode) {
//                    firstNode = nodeId;
//                }
//                writer.println("    " + nodeId + " [shape=box;label=\"" + node.getLayerName() + "\"]");
//                Chunk prev = node.getPrevious();
//                if (null != prev) {
//                    writer.println("    " + String.valueOf(prev.hashCode()) + " -> " + nodeId);
//                }
//                Chunk next = node.getNext();
//                if (null != next) {
//                    writer.println("    " + nodeId + " -> " + String.valueOf(next.hashCode()));
//                }
//            }
//            writer.println("   }");
//            writer.println("  " + chunkId + " -> " + firstNode);
//            writer.println(" }");
        }
        for (Link link : links) {
            writer.println("  " + link.draw());
        }
        writer.println(" fontname=Courier;");
        writer.println(" label=\"" + escape(comment) + "\n(" + Layer.GLOBAL_LAYER_NAME + "=Global Layer, " + Layer.BASE_LAYER_NAME + "=Unnamed Layer)\";");
        writer.println("}");
    }

    private String escape(String comment) {
        return comment;
    }

}
