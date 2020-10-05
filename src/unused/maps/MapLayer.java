package com.efsol.tagml.model.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Layer;

public class MapLayer implements Layer {
	public final String name;
	private List<Chunk> chunks;

	public MapLayer(String name) {
		this.name = name;
		this.chunks = new ArrayList<>();
	}

	public void add(Chunk chunk) {
		chunks.add(chunk);
	}

	@Override
	public Object walk(ChunkVisitor visitor) {
		return walkForwards(visitor);
	}

	class DumpVisitor implements ChunkVisitor {
		@Override
		public Object visit(Chunk chunk) {
			System.out.println(" " + chunk);
			return true;
		}
	}

	public void dump() {
		System.out.println("Dump Layer " + name);
		walkForwards(new DumpVisitor());
	}

	@Override
	public Object walkForwards(ChunkVisitor visitor) {
		for (Chunk chunk : chunks) {
			Object ret = visitor.visit(chunk);
			if (null != ret) {
				return ret;
			}
		}
		return null;
	}

	@Override
	public Iterator<Chunk> iterator() {
		return chunks.iterator();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object walkBackwards(ChunkVisitor visitor) {
		throw new UnsupportedOperationException("Map model cannot walk backwards");
	}
}
