package com.efsol.tagml;

public class Bead {
	public final Layer layer;
	public final Node prev;
	public final Node next;

	public Bead(final Layer layer, final Node prev, final Node next) {
		this.layer = layer;
		this.prev = prev;
		this.next = next;
	}
}
