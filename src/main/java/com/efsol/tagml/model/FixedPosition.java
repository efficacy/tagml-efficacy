package com.efsol.tagml.model;

public class FixedPosition extends Position {
	public FixedPosition(int row, int col) {
		super(row, col);
	}

	@Override
	public void step() {
		// do nothing
	}

	@Override
	public void newline() {
		// do nothing
	}

	@Override
	public Position snapshot() {
		return this;
	}
}
