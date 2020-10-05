package com.efsol.tagml.lex;

import com.efsol.tagml.model.Position;

public interface Token {
	TokenType getType();
	Position getPosition();
	boolean isEmpty();
}
