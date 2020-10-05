package com.efsol.tagml.model;

import java.util.Collection;

public interface Node {
	String getLayerName();
	Collection<Tag> getTags();
	Chunk getNext();
	Chunk getPrevious();
}
