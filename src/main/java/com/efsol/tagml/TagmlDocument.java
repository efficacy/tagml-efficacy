package com.efsol.tagml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TagmlDocument extends TagmlNode {
	private Map<String, Layer> layers = new HashMap<>();

	public TagmlDocument() {
		layers = new HashMap<>();
		Layer base = new Layer(Layer.BASE_LAYER_NAME);
		addLayer(base);
	}

	public void addLayer(Layer layer) {
		layers.put(layer.name, layer);
	}

	public Collection<Layer> getlayers() {
		return layers.values();
	}

	public Layer getBaseLayer() {
		// TODO Auto-generated method stub
		return layers.get("_");
	}

}
