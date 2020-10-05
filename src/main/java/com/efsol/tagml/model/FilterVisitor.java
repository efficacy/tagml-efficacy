package com.efsol.tagml.model;

public class FilterVisitor implements ChunkVisitor {
	private DocumentFilter filter;
	private ChunkCollector collector;

	public FilterVisitor(DocumentFilter filter, ChunkCollector collector) {
		this.filter = filter;
		this.collector = collector;
	}

	@Override
	public Object visit(Chunk chunk) {
		if (filter.accept(chunk)) {
			collector.collect(chunk);
		}
		return null;
	}
}