package export;

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

class Link {
    public String from;
    public String to;
    public String colour;
    public String label;
    public String style;

    public Link(String from, String to, String colour, String label, String style) {
        this.from = from;
        this.to = to;
        this.colour = colour;
        this.label = label;
        this.style = style;
    }

    public Link(String from, String to, String colour, String label) {
        this(from, to, colour, label, null);
    }

    public Link(String from, String to) {
        this(from, to, null, null, null);
    }

    public String draw() {
        StringBuilder ret = new StringBuilder();
        ret.append(from);
        ret.append(" -> ");
        ret.append(to);
        if (null != colour || null != label | null != style) {
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
            if (null != style) {
                if (had) {
                    ret.append(";");
                }
                ret.append("style=\"");
                ret.append(style);
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

//        links.add(new Link(prev.lookup(Layer.BASE_LAYER_NAME), chunkId(chunks.get(0)), layerColours.get(Layer.BASE_LAYER_NAME), Layer.BASE_LAYER_NAME));
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
            nb.append(" [shape=box;labelloc=t;label=\"");
            for (Node node : chunk.getLayers().values()) {
                String layerName = node.getLayerName();
                if (null == layerName) {
                    layerName = Layer.BASE_LAYER_NAME;
                }

                Collection<Tag> tags = node.getTags();
                if (!tags.isEmpty()) {
                    nb.append(layerName);
                    nb.append(": ");

                    boolean hadTag = false;
                    for (Tag tag : tags) {
                        if (hadTag) {
                            nb.append(",");
                        }
                        nb.append(tag.name);
                        hadTag = true;
                    }
                    nb.append("\\l");
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
            links.add(new Link(id, boxId, "gray", null, "dashed"));

            nb.append("\"]");
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
