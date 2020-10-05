package com.efsol.tagml.model.maps;

import com.efsol.tagml.model.ModelException;

@SuppressWarnings("serial")
public class MapModelException extends ModelException {
	public MapModelException(String message) {
		super(message);
	}
	public MapModelException(Throwable cause) {
		super(cause);
	}
	public MapModelException(String message, Throwable cause) {
		super(message, cause);
	}
}
