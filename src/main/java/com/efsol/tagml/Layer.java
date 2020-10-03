package com.efsol.tagml;

import java.util.ArrayList;
import java.util.List;

public class Layer {
	public static final String BASE_LAYER_NAME = "_";
	public static final String GLOBAL_LAYER_NAME = "$";

	public final String name;
	private List<Node> nodes;

	public Layer(String name) {
		this.name = name;
		this.nodes = new ArrayList<>();
	}

	public void add(Node node) {
		nodes.add(node);
	}

	public void walk(NodeVisitor visitor) {
		for (Node node : nodes) {
			if (!visitor.visit(node)) {
				break;
			}
		}
	}
}
