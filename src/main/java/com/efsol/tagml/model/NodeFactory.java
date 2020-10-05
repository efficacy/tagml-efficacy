package com.efsol.tagml.model;

import java.util.Collection;

public interface NodeFactory {
	Node createNode(String layername, Collection<Tag> tags);
}
