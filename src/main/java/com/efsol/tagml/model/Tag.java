package com.efsol.tagml.model;

import java.util.Collection;

import com.efsol.util.Utils;

public class Tag {
    public final String name;
    public final String layer;
    public final Position position;
    public final String namespace;
    public final Collection<Annotation> annotations;

    public Tag(String type, String layer, Position position, String namespace, Collection<Annotation> annotations) {
        this.name = type;
        this.layer = layer;
        this.position = position;
        this.namespace = namespace;
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Tag(");
        ret.append(name);
        ret.append(",");
        ret.append(layer);
        ret.append(")");
        if (null != namespace) {
            ret.append(" ns=");
            ret.append(namespace);
        }
        if (null != annotations) {
            ret.append(" an=");
            ret.append(annotations);
        }
        return ret.toString();
    }

    public String toOpenString() {
        StringBuilder ret = new StringBuilder("[");
        ret.append(name);
        if (null != layer) {
            ret.append("|");
            ret.append(namespace);
        }
        ret.append(">");
        return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag))
            return false;
        Tag other = (Tag) obj;
        return Utils.same(this.name, other.name) && Utils.same(this.layer, other.layer) && Utils.same(this.namespace, other.namespace)
                && Utils.same(this.annotations, other.annotations);
    }

    @Override
    public int hashCode() {
        int ret = name.hashCode();
        if (null != layer) {
            ret += layer.hashCode();
            ;
        }
        if (null != namespace) {
            ret += namespace.hashCode();
        }
        if (null != annotations) {
            ret += annotations.hashCode();
        }
        return ret;
    }
}
