package com.efsol.tagml.model;

import java.util.Collection;

import com.efsol.util.Utils;

public class Tag {
	public final String name;
	public final String layer;
	public final String namespace;
	public final Collection<Annotation> annotations;

	public Tag(String type, String layer, String namespace, Collection<Annotation> annotations) {
		this.name = type;
		this.layer = layer;
		this.namespace = namespace;
		this.annotations = annotations;
	}

	@Override
	public String toString() {
		return "Tag(" + name + "," + layer + ") ns=" + namespace + " an=" + annotations;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		return Utils.same(this.name,other.name) && Utils.same(this.layer,other.layer) && Utils.same(this.namespace,other.namespace)
				&& Utils.same(this.annotations,other.annotations);
	}

	@Override public int hashCode() {
		int ret = name.hashCode();
		if (null != layer) {
			ret += layer.hashCode();;
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
