package com.efsol.tagml;

public class Layer {
	public static final String BASE_LAYER_NAME = "_";

	public final String name;
	private Node first;

	public Layer(String name) {
		this.name = name;
		this.first = null;
	}

	public void walk(NodeVisitor visitor) {
		Node p = first;
		while (null != p) {
			visitor.visit(p);
			p = p.next(name);
		}
	}
}
