package com.efsol.tagml;

import java.util.Collection;

public class Node {
	private final String value;
	private final Position position;
	private final Collection<String> layers;

	public Node(String value, Position position, Collection<String> layers) {
		this.value = value;
		this.position = position;
		this.layers = layers;
	}

	public String getValue() {
		return value;
	}

	public Position getPosition() {
		return position;
	}

	public Collection<String> getLayers() {
		return layers;
	}

	@Override
	public String toString() {
		return "Node(" + value + ")" + layers + " at " + position;
	}
}
