package com.efsol.tagml;

import java.util.HashMap;
import java.util.Map;

public class NodeBuffer {
	private StringBuilder buffer;
	private Map<String, Bead> context;
	private Position position;

	public NodeBuffer(Position position) {
		this.buffer = new StringBuilder();
		this.context = new HashMap<>();
		this.position = position.snapshot();
	}

	public void joinLayer(String name) {
		// TODO
	}

	public void leaveLayer(String name) {
		// TODO
	}

	public void append(String text) {
		this.buffer.append(text);
	}

	public Node swap(Position newPosition) {
		String text = buffer.toString();
		buffer.setLength(0);
		Node ret = new Node(text, context, position);
		this.position = newPosition;
		return ret;
	}

	public boolean isIncomplete() {
		return buffer.length() > 0;
	}
}
