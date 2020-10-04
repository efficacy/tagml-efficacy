package com.efsol.tagml;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.efsol.util.Utils;

public class TagmlDocument extends TagmlNode {
	private Map<String, Layer> layers = new HashMap<>();
	private Layer global;

	public TagmlDocument() {
		layers = new HashMap<>();
		global = new Layer(Layer.GLOBAL_LAYER_NAME);
	}

	public void addLayer(Layer layer) {
		layers.put(layer.name, layer);
	}

	public Collection<Layer> getlayers() {
		return layers.values();
	}

	public void addNode(Node node) {
//		System.out.println("document add node " + node);
		if (null != node) {
			// all nodes added to global
			global.add(node);
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

	public String spoolAsText(DocumentFilter filter) {
		StringBuilder ret = new StringBuilder();
		NodeCollector collector = new NodeCollector() {
			@Override
			public void collect(Node node) {
				ret.append(node.getValue());
			}
		};
		FilterVisitor visitor = new FilterVisitor(filter, collector);
		global.walk(visitor);
		return ret.toString();
	}

	private Set<Tag> mergeTags(Node node, DocumentFilter filter) {
		Set<Tag> ret = new HashSet<>();
		Map<String, Collection<Tag>> layers = node.getLayers();
		for (String layer : layers.keySet()) {
			if (filter.acceptLayer(layer)) {
				Collection<Tag> tags = layers.get(layer);
				for (Tag tag : tags) {
					ret.add(tag);
				}
			}
		}
		return ret;
	}

	public String spoolAsMarkup(DocumentFilter filter) {
		StringBuilder ret = new StringBuilder();
		Set<Tag> runningContext = new HashSet<>();
		NodeCollector collector = new NodeCollector() {
			@Override
			public void collect(Node node) {
				Set<Tag> newContext = mergeTags(node, filter);
				Set<Tag> removes = Utils.difference(runningContext, newContext);
				for (Tag tag : removes) {
					ret.append(tag.asCloseMarkup());
				}
				Set<Tag> adds = Utils.difference(newContext, runningContext);
				for (Tag tag : adds) {
					ret.append(tag.asOpenMarkup());
				}
				ret.append(node.getValue());
				runningContext.clear();
				runningContext.addAll(newContext);
			}
		};
		FilterVisitor visitor = new FilterVisitor(filter, collector);
		global.walk(visitor);
		for (Tag tag : runningContext) {
			ret.append(tag.asCloseMarkup());
		}
		return ret.toString();
	}
}
