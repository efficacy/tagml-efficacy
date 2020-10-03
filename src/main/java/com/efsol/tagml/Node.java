package com.efsol.tagml;

import java.util.Map;

public class Node {
	private String value;
	private Map<String, Bead> context;
	private Position position;

	public Node(String value, Map<String, Bead> context, Position position) {
		this.value = value;
		this.context = context;
		this.position = position;
	}

	public String getValue() {
		return value;
	}

	public Node next(String layer) {
		Bead bead = context.get(layer);
		if (null == bead) return null;
		return bead.next;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Node[" + value + " at (" + position.row + "," + position.col + ")]";
	}

	public Map<String, Bead> getContext() {
		return context;
	}
}
