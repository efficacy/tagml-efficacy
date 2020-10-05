package com.efsol.tagml.model;

public interface DocumentFilter {
	boolean accept(Chunk chunk);
	default boolean acceptLayer(String layer) {
		return true;
	}
}
