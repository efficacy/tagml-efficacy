package com.efsol.tagml.lex;

import com.efsol.tagml.Position;

public class OpenToken extends AbstractToken {
	private final String name;
	private final String layer;

	public OpenToken(String name, String layer, Position position) {
		super(TokenType.OPEN, position);
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
		return "OPEN[" + name + "] at " + getPosition();
	}

	public String getLayer() {
		return layer;
	}
}
