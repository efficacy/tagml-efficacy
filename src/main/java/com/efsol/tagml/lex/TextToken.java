package com.efsol.tagml.lex;

import com.efsol.tagml.Position;

public class TextToken extends AbstractToken {
	private final String text;

	public TextToken(String text, Position position) {
		super(TokenType.TEXT, position);
		this.text = text;
	}

	@Override
	public boolean isEmpty() {
		return text.isEmpty();
	}

	public String getText() {
		return text;
	}

	@Override public String toString() {
		return "TEXT[" + text + "]";
	}
}
