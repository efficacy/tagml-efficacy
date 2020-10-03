package com.efsol.tagml;

import java.util.HashMap;
import java.util.Map;

public class Node {
	private String value;
	private Map<String, Bead> context;

	public Node(String value) {
		this.value = value;
		this.context = new HashMap<>();
	}

	public String getValue() {
		return value;
	}

	public Node next(String layer) {
		Bead bead = context.get(layer);
		if (null == bead) return null;
		return bead.next;
	}

}
