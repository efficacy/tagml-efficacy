package com.efsol.tagml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParseContext {
	private Lexer lexer;
	private Map<String, LayerContext> layers;

	public ParseContext() {
		this.layers = new HashMap<>();
	}

	public Collection<LayerContext> getLayers() {
		return layers.values();
	}

	public Tag addTag(String layer, String type, String namespace, Annotation annotation) {
		LayerContext context = layers.get(layer);
		if (null == context) {
			throw new ParseException("attempt to add tag to unknown layer: " + layer, lexer.getPosition());
		}
		Tag tag = new Tag(type, namespace, annotation);
		context.add(tag);
		return tag;
	}

	public Tag removeTag(String layer, String type, Position position) {
		LayerContext context = layers.get(layer);
		if (null == context) {
			throw new ParseException("attempt to add tag to unknown layer: " + layer, lexer.getPosition());
		}
		Tag tag = context.removeTag(type);
		if (null == tag) {
			throw new ParseException("attempt to close tag " + type + " not known on layer: " + layer, lexer.getPosition());
		}
		return tag;
	}
}
