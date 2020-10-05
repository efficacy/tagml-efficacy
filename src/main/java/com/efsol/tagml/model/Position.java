package com.efsol.tagml.model;

public class Position {
	public int row;
	public int col;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Position() {
		this(1,0);
	}

	public void step() {
		++col;
	}

	public void cr() {
		col = 1;
	}

	public void newline() {
		++row;
		col = 1;
	}

	public Position snapshot() {
		return new FixedPosition(row, col);
	}

	@Override
	public String toString() {
		return "(" + row + "," + col + ")";
	}
}
