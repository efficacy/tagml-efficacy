package com.efsol.tagml;

public class Tag {
	public final String type;
	public final String namespace;
	public final Annotation annotation;

	public Tag(String type, String namespace, Annotation annotation) {
		this.type = type;
		this.namespace = namespace;
		this.annotation = annotation;
	}
}
