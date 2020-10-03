package com.efsol.tagml;

@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
	public final Position position;

	public ParseException(String message, Position position) {
		super(message);
		this.position = position.snapshot();
	}
}
