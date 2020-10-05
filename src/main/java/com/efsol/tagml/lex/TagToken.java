package com.efsol.tagml.lex;

import com.efsol.tagml.model.Position;

public abstract class TagToken extends AbstractToken {
	protected final String name;
	protected final String layer;

	public TagToken(TokenType type, String name, String layer, Position position) {
		super(type, position);
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

	public String getLayer() {
		return layer;
	}
}
