package com.efsol.tagml.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.efsol.tagml.Position;

public class Lexer {
	public static boolean verbose = false;

	/** states **/
	private final int OUT = 0x100;
	private final int O_OR_S = 0x200;
	private final int OS_LAYER = 0x300;
	private final int C_OR_A = 0x400;
	private final int CA_LAYER = 0x500;

	/** character types **/
	private final int OTHER = 0;
	private final int OPEN_SQ = 1;
	private final int OPEN_ANG = 2;
	private final int CLOSE_SQ = 3;
	private final int CLOSE_ANG = 4;
	private final int VBAR = 5;
	private final int PLUS = 6;
	private final int MINUS = 7;
	private final int QUERY = 8;
	private final int WHITESPACE = 9;

	private final int ESC_OFF = 0;
	private final int ESC_ON = 0x1000;

	private Reader input;
	private Position position;
	private boolean esc;
	private int state;

	public Lexer(Reader input) {
		this.input = input;
		this.position = new Position();
		this.esc = false;
		this.state = OUT;
	}

	public Position getPosition() {
		return position;
	}

	private int nextChar(boolean allowEscape) throws IOException {
		if (allowEscape)
			esc = false;
		int c = input.read();
		switch (c) {
		case '\r':
			position.newline();
			break;
		case '\n':
			position.newline();
			break;
		case '\\':
			position.step();
			if (allowEscape) {
				esc = true;
				return nextChar(false);
			}
			break;
		case -1:
			c = 0;
			break;
		default:
			position.step();
			break;
		}
		return c;
	}

	private int nextChar() throws IOException {
		return nextChar(true);
	}

	private int charType(int c) {
		switch (c) {
		case '[':
			return OPEN_SQ;
		case ']':
			return CLOSE_SQ;
		case '<':
			return OPEN_ANG;
		case '>':
			return CLOSE_ANG;
		case '!':
			return VBAR;
		case '+':
			return PLUS;
		case '-':
			return MINUS;
		case '?':
			return QUERY;
		default:
			if (Character.isWhitespace(c)) {
				return WHITESPACE;
			}
			return OTHER;
		}
	}

	class TokenContext {
		public TokenType type;
		public StringBuilder value;
		public Boolean optional;
		public Boolean pause;
		public StringBuilder name;
		public StringBuilder layer;
		public List<String> alternatives;

		public TokenContext() {
			this.value = new StringBuilder();
			this.name = new StringBuilder();
			this.layer = new StringBuilder();
			this.alternatives = new ArrayList<>();
			reset();
		}

		public void reset() {
			type = null;
			value.setLength(0);
			name.setLength(0);
			layer.setLength(0);
			alternatives.clear();
			optional = null;
			pause = null;
		}
	}

	private Token buildToken(TokenContext context) {
		if (null == context.type) return null;
		Token ret = null;
		switch (context.type) {
		case NONE:
			// return the null that is already there
			break;
		case TEXT:
			ret = new TextToken(context.value.toString(), position);
			break;
		case OPEN:
			// TODO
			break;
		case CLOSE:
			// TODO
			break;
		case SINGLE:
			// TODO
			break;
		case ALT:
			// TODO
			break;
		default:
			break;
		}
		context.reset();
		return ret;
	}

	public Token next() throws IOException {
		Token ret = null;
		TokenContext context = new TokenContext();

		for (int c = nextChar(); c != 0; c = nextChar()) {
			int ctype = charType(c);
//			log("state=" + state + " charType(" + (char)c + ")=" + ctype);
			switch (state + ctype) {
			case OUT + OTHER:
			case OUT + WHITESPACE:
			case OUT + VBAR:
			case OUT + PLUS:
			case OUT + MINUS:
			case OUT + QUERY:
				context.type = TokenType.TEXT;
				context.value.append((char) c);
//				log(" token type=" + context.type + " appended(" + (char)c + ") value=" + context.value);
				break;
			case OUT + OPEN_SQ:
				ret = buildToken(context);
				state = O_OR_S;
				break;
			case OUT + OPEN_ANG:
				state = C_OR_A;
				break;
			case O_OR_S + OTHER:

			}
			log("nextToken after loop ret=" + ret);
			// if we constructed a non-empty token, return it
			if (null != ret && !ret.isEmpty()) break;
		}

		// deal with possible trailing or unclosed text
		if (null == ret) {
			ret = buildToken(context);
			log("created trailing token: " + ret);
		}
		log("lexer returning: " + ret);
		return ret;
	}

	void log(String s) {
		if (verbose) System.out.println(s);
	}
}
