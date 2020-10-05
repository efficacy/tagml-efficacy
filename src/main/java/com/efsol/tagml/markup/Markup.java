package com.efsol.tagml.markup;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.FilterVisitor;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkCollector;
import com.efsol.tagml.model.Tag;
import com.efsol.util.Utils;

public class Markup {
	public static String asOpen(Tag tag) {
		StringBuilder ret = new StringBuilder("[");
		ret.append(tag.name);
		if (null != tag.layer) {
			ret.append("|");
			ret.append(tag.layer);
		}
		// TODO render namespace and annotations
		ret.append(">");
		return ret.toString();
	}

	public static String asClose(Tag tag) {
		StringBuilder ret = new StringBuilder("<");
		ret.append(tag.name);
		if (null != tag.layer) {
			ret.append("|");
			ret.append(tag.layer);
		}
		// TODO render namespace
		ret.append("]");
		return ret.toString();
	}


	public static String spoolAsText(Document document, DocumentFilter filter) {
		StringBuilder ret = new StringBuilder();
		ChunkCollector collector = new ChunkCollector() {
			@Override
			public void collect(Chunk chunk) {
				ret.append(chunk.getValue());
			}
		};
		FilterVisitor visitor = new FilterVisitor(filter, collector);
		document.walk(visitor);
		return ret.toString();
	}

	private static Set<Tag> mergeTags(Chunk chunk, DocumentFilter filter) {
		Set<Tag> ret = new HashSet<>();
		Map<String, Node> layers = chunk.getLayers();
		for (String layer : layers.keySet()) {
			if (filter.acceptLayer(layer)) {
				Node node = layers.get(layer);
				Collection<Tag> tags = node.getTags();
				for (Tag tag : tags) {
					ret.add(tag);
				}
			}
		}
		return ret;
	}

	public static String spoolAsMarkup(Document document, DocumentFilter filter) {
		StringBuilder ret = new StringBuilder();
		Set<Tag> runningContext = new HashSet<>();
		ChunkCollector collector = new ChunkCollector() {
			@Override
			public void collect(Chunk chunk) {
				Set<Tag> newContext = mergeTags(chunk, filter);
				Set<Tag> removes = Utils.difference(runningContext, newContext);
				for (Tag tag : removes) {
					ret.append(Markup.asClose(tag));
				}
				Set<Tag> adds = Utils.difference(newContext, runningContext);
				for (Tag tag : adds) {
					ret.append(Markup.asOpen(tag));
				}
				ret.append(chunk.getValue());
				runningContext.clear();
				runningContext.addAll(newContext);
			}
		};
		FilterVisitor visitor = new FilterVisitor(filter, collector);
		document.walk(visitor);
		for (Tag tag : runningContext) {
			ret.append(Markup.asClose(tag));
		}
		return ret.toString();
	}

}
