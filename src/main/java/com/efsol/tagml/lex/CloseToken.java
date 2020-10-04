package com.efsol.tagml.lex;

import com.efsol.tagml.Position;

public class CloseToken extends AbstractToken {
	private final String name;
	private final String layer;

	public CloseToken(String name, String layer, Position position) {
		super(TokenType.CLOSE, position);
		this.name = name;
		this.layer = layer;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		return name;
	}

	@Override public String toString() {
		return "CLOSE[" + name + "] at " + getPosition();
	}

	public String getLayer() {
		return layer;
	}
}
