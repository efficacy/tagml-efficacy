package export.dot;

public class Link {
    public String from;
    public String to;
    public String colour;
    public String label;
    public String extra;

    public Link(String from, String to, String colour, String label, String extra) {
        this.from = from;
        this.to = to;
        this.colour = colour;
        this.label = label;
        this.extra = extra;
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
        if (null != colour || null != label | null != extra) {
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
            if (null != extra) {
                if (had) {
                    ret.append(";");
                }
                ret.append(extra);
                had = true;
            }
            ret.append("]");
        }
        return ret.toString();
    }
}