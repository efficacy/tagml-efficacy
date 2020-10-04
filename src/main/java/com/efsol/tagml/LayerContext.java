package com.efsol.tagml;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LayerContext {
	private String name;
	private List<Tag> tags;

	public LayerContext(String name) {
		this.name = name;
		this.tags = new LinkedList<>();
	}

	public void add(Tag tag) {
		tags.add(tag);
	}

	// ** remove most recent instance of this tag type **/
	public Tag removeTag(String type) {
		Tag found = findTag(type);
		if (null != found) {
			tags.remove(found);
		}
		return found;
	}

	private Tag findTag(String type) {
		Tag found = null;
		ListIterator<Tag> li = tags.listIterator(tags.size());
		while (li.hasPrevious()) {
			Tag tag = li.previous();
			if (tag.type.equals(type)) {
				found = tag;
				break;
			}
		}
		return found;
	}

	public String getName() {
		return name;
	}

	public Collection<Tag> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return "LayerContext(" + name + ")=" + tags;
	}

	public boolean isEmpty() {
		return tags.isEmpty();
	}
}
