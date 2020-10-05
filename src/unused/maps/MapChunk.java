package com.efsol.tagml.model.maps;

import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;

public class MapChunk extends Chunk {
	private Map<String, Node> nodes;

	public MapChunk(String value, Position position, Map<String, Node> nodes) {
		super(value, position);
		this.nodes = nodes;
	}

	@Override
	public Chunk getNext(String layerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chunk getPrevious(String layerName) {
		// TODO Auto-generated method stub
		return null;
	}

}
