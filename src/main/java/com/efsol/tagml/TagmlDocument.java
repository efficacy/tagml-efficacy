package com.efsol.tagml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TagmlDocument extends TagmlNode {
	private Map<String, Layer> layers = new HashMap<>();
	private Layer global;

	public TagmlDocument() {
		layers = new HashMap<>();
		global = new Layer(Layer.GLOBAL_LAYER_NAME);
		addLayer(global);
	}

	public void addLayer(Layer layer) {
		layers.put(layer.name, layer);
	}

	public Collection<Layer> getlayers() {
		return layers.values();
	}

	public void addNode(Node node) {
		// all nodes added to global
		global.add(node);

		// then add to any other layers
		for (Bead bead : node.getContext().values()) {
			layers.get(bead.layer.name).add(node);
		}
	}

	public Layer getGlobalLayer() {
		return global;
	}

	public Layer getLayer(String name) {
		return layers.get(name);
	}

	@Override
	public String toString() {
		return "DOC[nlayers=" + layers.size() + "]";
	}
}
