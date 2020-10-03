package com.efsol.tagml;

import java.io.Reader;
import java.util.Iterator;

public class Lexer implements Iterator<LexToken>{
	private Position position;

	public Lexer(Reader input) {
		this.position = new Position();
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LexToken next() {
		// TODO Auto-generated method stub
		return null;
	}
}
