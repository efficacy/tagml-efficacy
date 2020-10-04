package com.efsol.tagml.lex;

import com.efsol.tagml.Position;

public abstract class AbstractToken implements Token {
	private final TokenType type;;
	private final Position position;

	public AbstractToken(TokenType type, Position position) {
		this.type = type;
		this.position = position;
		System.out.println("creating Token(" + type + ") at " + position);
	}

	@Override
	public TokenType getType() {
		return type;
	}

	@Override
	public Position getPosition() {
		return position;
	}
}
