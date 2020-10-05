package com.efsol.tagml.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.efsol.tagml.ParseException;
import com.efsol.tagml.model.Position;

public class Lexer {
	public static boolean verbose = false;

	/** states **/
	public static final int OUT = 0x100;
	public static final int O_OR_S = 0x200;
	public static final int OS_LAYER = 0x300;
	public static final int C_OR_A = 0x400;
	public static final int C_LAYER = 0x500;
	public static final int A_LAYER = 0x600;

	/** character types **/
	public static final int OTHER = 0;
	public static final int OPEN_SQ = 1;
	public static final int OPEN_ANG = 2;
	public static final int CLOSE_SQ = 3;
	public static final int CLOSE_ANG = 4;
	public static final int VBAR = 5;
	public static final int PLUS = 6;
	public static final int MINUS = 7;
	public static final int QUERY = 8;
	public static final int WHITESPACE = 9;

	private TokenContext context;
	private Reader input;
	private Position position;
	private boolean esc;
	private int state;

	public Lexer(Reader input) {
		this.context = new TokenContext();
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

	public static int charType(int c) {
		switch (c) {
		case '[':
			return OPEN_SQ;
		case ']':
			return CLOSE_SQ;
		case '<':
			return OPEN_ANG;
		case '>':
			return CLOSE_ANG;
		case '|':
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

	public static

	class TokenContext {
		public TokenType type;
		public Position position;
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
			position = null;
			value.setLength(0);
			name.setLength(0);
			layer.setLength(0);
			alternatives.clear();
			optional = null;
			pause = null;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		@Override
		public String toString() {
			return "TokenContext[" + type + "] name=" + name + ", value=" + value + ", layer=" + layer + ", pos="
					+ position;
		}
	}

	private Token buildToken(TokenContext context) {
		log("buildToken ctx=" + context);
		if (null == context.type) {
			throw new IllegalArgumentException("attempt to build invcalid ctx " + context);
//			return null;
		}
		String layer = context.layer.length() > 0 ? context.layer.toString() : null;
		Token ret = null;
		switch (context.type) {
		case TEXT:
			ret = new TextToken(context.value.toString(), context.position);
			break;
		case OPEN:
			log("biulding open ctx=" + context);
			ret = new OpenToken(context.name.toString(), layer, context.position);
			break;
		case CLOSE:
			log("biulding close ctx=" + context);
			ret = new CloseToken(context.name.toString(), layer, context.position);
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
		log("buildToken returning " + ret);
		return ret;
	}

	private Token flushText() {
		Token ret = null;
		if (context.value.length() > 0) {
			context.type = TokenType.TEXT;
			ret = buildToken(context); // flush any pending text
		}
		return ret;
	}

	public Token next() throws IOException {
		Token ret = null;

		for (int i = nextChar(); i != 0; i = nextChar()) {
			int ctype = charType(i);
			char c = (char) i;
			log("state=" + stateName(state) + " charType(" + c + ")=" + ctype);
			switch (state + ctype) {

			// do nothing, ignore whitespace in tags
			case O_OR_S + WHITESPACE:
			case C_OR_A + WHITESPACE:
			case OS_LAYER + WHITESPACE:
			case C_LAYER + WHITESPACE:
			case A_LAYER + WHITESPACE:
				break;

			// accumulate text outside tags
			case OUT + OTHER:
			case OUT + WHITESPACE:
			case OUT + VBAR:
			case OUT + PLUS:
			case OUT + MINUS:
			case OUT + QUERY:
			case OUT + CLOSE_SQ:
			case OUT + CLOSE_ANG:
				if (null == context.type) {
					context.type = TokenType.TEXT;
					context.setPosition(position.snapshot());
//					log("starting text at " + context.position);
				}
				context.value.append(c);
				log(" token type=" + context.type + " appended(" + c + ") value=" + context.value);
				break;

			case OUT + OPEN_SQ:
				ret = flushText();
				context.setPosition(position.snapshot());
				context.type = TokenType.OPEN;
				log("entering open or single, context pos=" + context.position);
				state = O_OR_S;
				break;
			case OUT + OPEN_ANG:
				ret = flushText();
				context.setPosition(position.snapshot());
				context.type = TokenType.CLOSE;
				log("entering close or alt, context pos=" + context.position);
				state = C_OR_A;
				break;

			case O_OR_S + CLOSE_ANG:
				ret = buildToken(context); // build the open tag
				state = OUT;
				break;
			case O_OR_S + CLOSE_SQ:
				context.type = TokenType.SINGLE;
				ret = buildToken(context); // build the open tag
				state = OUT;

			case O_OR_S + OTHER:
				context.name.append(c);
				break;
			case O_OR_S + VBAR:
				state = OS_LAYER;
				break;
			case OS_LAYER + OTHER:
				context.layer.append(c);
				break;
			case OS_LAYER + PLUS:
				// ignore plus and autocreate for now
				break;
			case OS_LAYER + CLOSE_ANG:
				ret = buildToken(context); // build the open tag
				state = OUT;
				break;
			case OS_LAYER + CLOSE_SQ:
				context.type = TokenType.SINGLE;
				ret = buildToken(context); // build the open tag
				state = OUT;
				break;

			case C_OR_A + CLOSE_SQ:
//			log("building close token, context pos=" + context.position);
				ret = buildToken(context); // build the close tag
				state = OUT;
				break;
			case C_OR_A + OTHER:
				if (null == context.type) {
					context.type = TokenType.CLOSE;
				}
				context.name.append(c);
				break;
			case C_OR_A + VBAR:
				if (null == context.type) {
					context.type = TokenType.ALT;
					state = A_LAYER;
				} else {
					state = C_LAYER;
				}
				break;
			case C_LAYER + OTHER:
			case A_LAYER + OTHER:
				context.layer.append(c);
				break;
			case A_LAYER + VBAR:
				String alt = context.layer.toString();
				context.alternatives.add(alt);
				context.layer.setLength(0);
				break;
			case C_LAYER + CLOSE_SQ:
				ret = buildToken(context); // build the open tag
				state = OUT;
				break;
			case A_LAYER + CLOSE_ANG:
				ret = buildToken(context); // build the open tag
				state = OUT;
				break;

			default:
				throw new ParseException("unexpected character " + c + " in state " + state, position);
			}
			log("after switch, state=" + stateName(state) + " ctx=" + context);
//			log("nextToken after loop ret=" + ret);
			// if we constructed a non-empty token, return it
			if (null != ret && !ret.isEmpty())
				break;
		}

		// deal with possible trailing or unclosed text
		if (null == ret) {
			if (null != context.type) {
				ret = buildToken(context);
				log("created trailing token: " + ret);
			}
		}
		log("lexer returning: " + ret);
		return ret;
	}

	void log(String s) {
		if (verbose)
			System.out.println(s);
	}

	public static String stateName(int state) {
		switch (state) {
		case OUT:
			return "OUT";
		case O_OR_S:
			return "O/S";
		case OS_LAYER:
			return "OS_LAYER";
		case C_OR_A:
			return "C/A";
		case C_LAYER:
			return "C_LAYER";
		case A_LAYER:
			return "A_LAYER";
		default:
			return "UNKNOWN";
		}
	}
}
