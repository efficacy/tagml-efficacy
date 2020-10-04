package com.efsol.tagml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.OpenToken;
import com.efsol.tagml.lex.Token;

public class ParseContext {
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

	public void joinLayer(String name) {
		// TODO
	}

	public void leaveLayer(String name) {
		// TODO
	}

	public void append(String text) {
		this.buffer.append(text);
	}

	public Node swap() {
		System.out.println("ParseContext.swap pos=" + position);
		Node ret = null;
		if (buffer.length() > 0) {
			String text = buffer.toString();
			buffer.setLength(0);
			ret = new Node(text, position, null);
		}
		this.position = null;
		return ret;
	}

	public boolean isIncomplete() {
		return buffer.length() > 0;
	}

	public void setPosition(Position position) {
		System.out.println("setPosition old=" + this.position + " new=" + position);
		if (null == this.position) {
			this.position = position;
		}
	}
}
