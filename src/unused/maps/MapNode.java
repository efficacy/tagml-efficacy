package com.efsol.tagml.model.maps;

import java.util.Collection;
import java.util.Map;

import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;

public class MapNode {
	public static boolean verbose = false;
	private final String value;
	private final Position position;
	private final Map<String, Collection<Tag>> layers;

	public MapNode(String value, Position position, Map<String, Collection<Tag>> layers) {
		log("creating Node(" + value + ") " + layers + " at " + position);
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

	public Map<String, Collection<Tag>> getLayers() {
		return layers;
	}

	public boolean isOnLayer(String layerName) {
		Collection<Tag> layer = layers.get(layerName);
		return null != layer && !layer.isEmpty();
	}

	@Override
	public String toString() {
		return "Node(" + value + ")" + layers + " at " + position;
	}

	void log(String s) {
		if (verbose) System.out.println("Node::" + s);
	}
}
