package com.efsol.tagml;

public interface DocumentFilter {
	boolean accept(Node node);
	default boolean acceptLayer(String layer) {
		return true;
	}
}
