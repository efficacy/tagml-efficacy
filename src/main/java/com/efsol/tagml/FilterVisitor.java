package com.efsol.tagml;

public class FilterVisitor implements NodeVisitor {
	private DocumentFilter filter;
	private NodeCollector collector;

	public FilterVisitor(DocumentFilter filter, NodeCollector collector) {
		this.filter = filter;
		this.collector = collector;
	}

	@Override
	public boolean visit(Node node) {
		if (filter.accept(node)) {
			collector.collect(node);
		}
		return true;
	}
}