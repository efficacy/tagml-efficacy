package com.efsol.tagml.lex;

import com.efsol.tagml.model.Position;

public class OpenToken extends TagToken {
	public OpenToken(String name, String layer, Position position) {
		super(TokenType.OPEN, name, layer, position);
	}

	@Override public String toString() {
		return "OPEN[" + name + "," + layer + "] at " + getPosition();
	}
}
