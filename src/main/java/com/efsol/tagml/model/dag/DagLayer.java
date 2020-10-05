package com.efsol.tagml.model.dag;

import java.util.Collections;
import java.util.Iterator;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.NodeFactory;

public class DagLayer implements Layer {
	public static boolean verbose = false;
	private final NodeFactory factory;
	private final String name;
	private Chunk first;
	private Chunk last;

	public DagLayer(final String name, NodeFactory factory) {
		this.name = name;
		this.factory = factory;
		this.first = null;
		this.last = null;
	}

	@Override
	public String getName() {
		return name;
	}

	public void add(Chunk chunk) {
		if (null == chunk) {
			throw new DagModelException("attempt to add null chunk to layer(" + name +")");
		}
		log("DagLayer(" + name + ") add chunk " + chunk);

		DagNode node = (DagNode)chunk.getLayerNode(name);
		if (null == node) {
			node = (DagNode)factory.createNode(name, Collections.emptyList());
			chunk.addLayer(name, node);
		}

		if (null == first) {
			this.first = chunk;
			this.last = chunk;
		} else {
			node.prev = this.last;
			DagNode prev = (DagNode)this.last.getLayerNode(name);
			prev.next = chunk;
			this.last = chunk;
		}

		chunk.addLayer(name, node);
	}

	@Override
	public Object walkForwards(ChunkVisitor visitor) {
		Chunk chunk = first;
		while (null != chunk) {
			Object ret = visitor.visit(chunk);
			if (null != ret) {
				return ret;
			}
			chunk = chunk.getNext(name);
			if (chunk == first) throw new IllegalStateException("recursive?");
		}
		return null;
	}

	@Override
	public Object walkBackwards(ChunkVisitor visitor) {
		Chunk chunk = last;
		while (null != chunk) {
			Object ret = visitor.visit(chunk);
			if (null != ret) {
				return ret;
			}
			chunk = chunk.getPrevious(name);
		}
		return null;
	}

	private class ChunkIterator implements Iterator<Chunk> {
		private Chunk chunk;

		public ChunkIterator(Chunk chunk) {
			this.chunk = chunk;
		}

		@Override
		public boolean hasNext() {
			return null == chunk || null == chunk.getNext(name);
		}

		@Override
		public Chunk next() {
			return chunk.getNext(name);
		}
	}

	@Override
	public Iterator<Chunk> iterator() {
		return new ChunkIterator(first);
	}

	@Override
	public String toString() {
		return "DAGLayer(" + name + ") first=" + first;
	}

	void log(String s) {
		if (verbose) System.out.println("Daglayer::" + s);
	}
}
