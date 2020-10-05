package com.efsol.tagml.model.dag;

import java.util.Collection;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;

public class DagNode implements Node {
	private final DagLayer layer;
	private final Collection<Tag> tags;
	protected Chunk prev;
	protected Chunk next;

	public DagNode(final DagLayer layer, final Collection<Tag> tags, final Chunk prev, final Chunk next) {
		this.layer = layer;
		this.tags = tags;
		this.prev = prev;
		this.next = next;
	}

	public DagLayer getHead() {
		return layer;
	}

	@Override
	public Chunk getPrevious() {
		return prev;
	}

	@Override
	public Chunk getNext() {
		return next;
	}

	@Override
	public Collection<Tag> getTags() {
		return tags;
	}

	@Override
	public String getLayerName() {
		return layer.getName();
	}

	@Override
	public String toString() {
		String name = null == layer ? "null" : layer.getName();
		return "DAGNode(" + name + ") tags=" + tags + ")";
	}
}
