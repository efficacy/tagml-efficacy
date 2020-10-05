package com.efsol.tagml.lex;

import com.efsol.tagml.model.Position;

public class CloseToken extends TagToken {
	public CloseToken(String name, String layer, Position position) {
		super(TokenType.CLOSE, name, layer, position);
	}

	@Override public String toString() {
		return "CLOSE[" + name + "," + layer + "] at " + getPosition();
	}
}
