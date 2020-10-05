package com.efsol.tagml.model.dag;

import com.efsol.tagml.model.ModelException;

@SuppressWarnings("serial")
public class DagModelException extends ModelException {
	public DagModelException(String message) {
		super(message);
	}
	public DagModelException(Throwable cause) {
		super(cause);
	}
	public DagModelException(String message, Throwable cause) {
		super(message, cause);
	}
}
