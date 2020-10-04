package com.efsol.tagml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.OpenToken;
import com.efsol.tagml.lex.Token;

public class ParseContext {
	public static boolean verbose = false;
	private Map<String, LayerContext> layers;
	private StringBuilder buffer;
	private Position position;

	public ParseContext(TagmlDocument doc) {
		this.layers = new HashMap<>();
		this.buffer = new StringBuilder();
		this.position = null;
		for (Layer layer: doc.getlayers()) {
			layers.put(layer.name, new LayerContext(layer.name));
		}
	}

	public Collection<LayerContext> getLayers() {
		return layers.values();
	}

	public Tag addTag(String name, String layer, Position position) {
		if (null == layer) layer = Layer.BASE_LAYER_NAME;
		log("addTag(" + name + "," + layer + ") at " + position);
		LayerContext context = layers.get(layer);
		if (null == context) {
			throw new ParseException("attempt to add tag to unknown layer: " + layer, position);
		}
		Tag tag = new Tag(name, null, null);
		context.add(tag);
		return tag;
	}

	public Tag removeTag(String name, String layer, Position position) {
		if (null == layer) layer = Layer.BASE_LAYER_NAME;
		log("removeTag(" + name + "," + layer + ") at " + position);
		LayerContext context = layers.get(layer);
		if (null == context) {
			throw new ParseException("attempt to remove tag from unknown layer: " + layer, position);
		}
		Tag tag = context.removeTag(name);
		if (null == tag) {
			throw new ParseException("attempt to close tag " + name + " not known on layer: " + layer, position);
		}
		return tag;
	}

	public void append(String text) {
		this.buffer.append(text);
	}

	public Node swap() {
		log("swap pos=" + position);
		Node ret = null;
		if (buffer.length() > 0) {
			String text = buffer.toString();
			buffer.setLength(0);
			Map<String, Collection<Tag>> nodeLayers = new HashMap<>();
			for (String layerName : this.layers.keySet()) {
				List<Tag> layer = new ArrayList<>();
				for (Tag tag : this.layers.get(layerName).getTags()) {
					layer.add(tag);
				}
				nodeLayers.put(layerName, layer);
			}
			ret = new Node(text, position, nodeLayers);
		}
		this.position = null;
		log("swap returning " + ret);
		return ret;
	}

	public boolean isIncomplete() {
		return buffer.length() > 0;
	}

	public void setPosition(Position position) {
		log("setPosition old=" + this.position + " new=" + position);
		if (null == this.position) {
			this.position = position;
		}
	}

	@Override
	public String toString() {
		return "ParseContext(" + buffer.toString() + ") " + layers + " at " + position;
	}

	void log(String s) {
		if (verbose) System.out.println("ParseContext::" + s);
	}
}
