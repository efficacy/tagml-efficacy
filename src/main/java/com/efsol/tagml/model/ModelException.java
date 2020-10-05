package com.efsol.tagml.model;

@SuppressWarnings("serial")
public class ModelException extends IllegalStateException {
	public ModelException(String message) {
		super(message);
	}
	public ModelException(Throwable cause) {
		super(cause);
	}
	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}
}
