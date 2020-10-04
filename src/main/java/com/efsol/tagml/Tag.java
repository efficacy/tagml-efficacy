package com.efsol.tagml;

import com.efsol.util.Utils;

public class Tag {
	public final String type;
	public final String layer;
	public final String namespace;
	public final Annotation annotation;

	public Tag(String type, String layer, String namespace, Annotation annotation) {
		this.type = type;
		this.layer = layer;
		this.namespace = namespace;
		this.annotation = annotation;
	}

	@Override
	public String toString() {
		return "Tag(" + type + "," + layer + ") ns=" + namespace + " an=" + annotation;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		return Utils.same(this.type,other.type) && Utils.same(this.layer,other.layer) && Utils.same(this.namespace,other.namespace)
				&& Utils.same(this.annotation,other.annotation);
	}

	@Override public int hashCode() {
		int ret = type.hashCode() + layer.hashCode();
		if (null != namespace) {
			ret += namespace.hashCode();
		}
		if (null != annotation) {
			ret += annotation.hashCode();
		}
		return ret;
	}

	public Object asOpenMarkup() {
		StringBuilder ret = new StringBuilder("[");
		ret.append(type);
		if (!layer.equals(Layer.BASE_LAYER_NAME)) {
			ret.append("|");
			ret.append(layer);
		}
		// TODO render namespace and annotations
		ret.append(">");
		return ret.toString();
	}

	public Object asCloseMarkup() {
		StringBuilder ret = new StringBuilder("<");
		ret.append(type);
		if (!layer.equals(Layer.BASE_LAYER_NAME)) {
			ret.append("|");
			ret.append(layer);
		}
		// TODO render namespace
		ret.append("]");
		return ret.toString();
	}
}
