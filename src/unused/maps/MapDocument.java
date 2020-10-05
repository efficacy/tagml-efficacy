package com.efsol.tagml.model.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;

public class MapDocument implements DocumentFactory, Document {
	private Map<String, Layer> layers = new HashMap<>();
	private MapLayer global;

	public MapDocument() {
		reset();
	}

	@Override
	public void reset() {
		this.layers = new HashMap<>();
		this.global = new MapLayer(Layer.GLOBAL_LAYER_NAME);
	}

	@Override
	public void addLayer(Layer layer) {
		layers.put(layer.getName(), layer);
	}

	public Collection<Layer> getlayers() {
		return layers.values();
	}

	@Override
	public void addChunk(Chunk chunk) {
		if (null == chunk) {
			throw new MapModelException("attempt to add null chunk");
		}
		global.add(chunk);
	}

	@Override
	public Layer getGlobalLayer() {
		return global;
	}

	@Override
	public Layer getLayer(String name) {
		return layers.get(name);
	}

	@Override
	public Object walkAllForwards(ChunkVisitor visitor) {
		return global.walkForwards(visitor);
	}

	@Override
	public Object walkAllBackwards(ChunkVisitor visitor) {
		return global.walkBackwards(visitor);
	}

	@Override
	public String toString() {
		return "DOC[nlayers=" + layers.size() + "]";
	}

	@Override
	public Chunk createChunk(String text, Position position, Map<String, Node> nodes) {
		return new MapChunk(text, position, nodes);
	}

	@Override
	public Node createNode(String layername, Collection<Tag> tags) {
		return new MapNode(value, position, layers)
	}

	@Override
	public Document createDocument() {
		// TODO Auto-generated method stub
		return null;
	}
}
