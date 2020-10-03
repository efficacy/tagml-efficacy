package com.efsol.tagml;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LayerContext {
	private List<Tag> tags;

	public LayerContext() {
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
}