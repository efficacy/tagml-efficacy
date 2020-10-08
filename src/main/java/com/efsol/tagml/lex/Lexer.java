package com.efsol.tagml.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.efsol.tagml.model.Position;
import com.efsol.tagml.parser.ParseException;

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
    public static final int COMMA = 9;
    public static final int WHITESPACE = 20;

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

    public boolean isEscaped() {
        return esc;
    }

    private int nextChar(boolean allowEscape) throws IOException {
        if (allowEscape) {
            esc = false;
        }
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
        case ',':
            return COMMA;
        default:
            if (Character.isWhitespace(c)) {
                return WHITESPACE;
            }
            return OTHER;
        }
    }

    public static

    class TokenContext {
        public StringBuilder buf;
        public TokenType type;
        public Position position;
        public String value;
        public Boolean optional;
        public Boolean pause;
        public String name;
        public Collection<String> layers;
        public List<String> alternatives;

        public TokenContext() {
            this.buf = new StringBuilder();
            this.layers = new ArrayList<>();
            this.alternatives = new ArrayList<>();
            reset();
        }

        public void reset() {
            buf.setLength(0);
            value = null;
            type = null;
            position = null;
            name = null;
            layers.clear();
            alternatives.clear();
            optional = null;
            pause = null;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return "TokenContext[" + type + "] name=" + name + ", value=" + value + ", layers=" + layers + ", pos="
                    + position;
        }
    }

    private Collection<String> snapshotLayers(Collection<String> layers) {
        Collection<String> ret = new ArrayList<>(layers.size());
        ret.addAll(layers);
        return ret;
    }

    private Token buildToken(TokenContext context) {
        log("buildToken ctx=" + context);
        if (null == context.type) {
            throw new IllegalArgumentException("attempt to build invcalid ctx " + context);
//			return null;
        }
        Token ret = null;
        switch (context.type) {
        case TEXT:
            ret = new TextToken(context.value, context.position);
            break;
        case OPEN:
            log("biulding open ctx=" + context);
            log("biulding open ctx.layers=" + context.layers);
            ret = new OpenToken(context.name, snapshotLayers(context.layers), context.position);
            log("biulding open created layers=" + ((TagToken) ret).getLayers());
            log("biulding open created tag=" + ret);
            break;
        case CLOSE:
            log("biulding close ctx=" + context);
            log("biulding close ctx.layers=" + context.layers);
            ret = new CloseToken(context.name, snapshotLayers(context.layers), context.position);
            log("biulding close created layers=" + ((TagToken) ret).getLayers());
            log("biulding close created tag=" + ret);
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

    private String claimBuffer(StringBuilder buf) {
        String ret = buf.toString();
        buf.setLength(0);
        return ret;
    }

    private Token flushText() {
        Token ret = null;
        if (context.buf.length() > 0) {
            context.value = claimBuffer(context.buf);
            context.type = TokenType.TEXT;
            ret = buildToken(context); // flush any pending text
        }
        return ret;
    }

    public Token next() throws IOException {
        Token ret = null;

        for (int i = nextChar(); i != 0; i = nextChar()) {
            int ctype = esc ? OTHER : charType(i);
            char c = (char) i;
            log("state=" + stateName(state) + " charType(" + c + ")=" + ctype);
            switch (state + ctype) {

            // do nothing, ignore whitespace in tags (for now)
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
            case OUT + COMMA:
            case OUT + CLOSE_SQ:
            case OUT + CLOSE_ANG:
                if (null == context.type) {
                    context.type = TokenType.TEXT;
                    context.setPosition(position.snapshot());
//					log("starting text at " + context.position);
                }
                context.buf.append(c);
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
                if (0 == context.buf.length()) {
                    throw new ParseException("Tag name cannot be empty", position);
                }
                context.name = claimBuffer(context.buf);
                ret = buildToken(context); // build the open tag
                state = OUT;
                break;
            case O_OR_S + CLOSE_SQ:
                if (0 == context.buf.length()) {
                    throw new ParseException("Tag name cannot be empty", position);
                }
                context.name = claimBuffer(context.buf);
                context.type = TokenType.SINGLE;
                ret = buildToken(context); // build the open tag
                state = OUT;

            case O_OR_S + OTHER:
                context.buf.append(c);
                break;
            case O_OR_S + VBAR:
                if (0 == context.buf.length()) {
                    throw new ParseException("Tag name cannot be empty", position);
                }
                context.name = claimBuffer(context.buf);
                state = OS_LAYER;
                break;
            case OS_LAYER + OTHER:
                context.buf.append(c);
                break;
            case OS_LAYER + PLUS:
                // ignore plus and autocreate for now
                break;
            case OS_LAYER + COMMA:
                if (0 == context.buf.length()) {
                    throw new ParseException("layer name cannot be empty", position);
                }
                String openLayer = claimBuffer(context.buf);
                context.layers.add(openLayer);
                break;
            case OS_LAYER + CLOSE_ANG:
                if (context.buf.length() > 0) {
                    context.layers.add(claimBuffer(context.buf));
                }
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
                if (0 == context.buf.length()) {
                    throw new ParseException("Tag name cannot be empty", position);
                }
                context.name = claimBuffer(context.buf);
                ret = buildToken(context); // build the close tag
                state = OUT;
                break;
            case C_OR_A + OTHER:
                context.buf.append(c);
                break;
            case C_OR_A + VBAR:
                if (context.buf.length() > 0) {
                    context.name = claimBuffer(context.buf);
                    context.type = TokenType.CLOSE;
                    state = C_LAYER;
                } else {
                    context.type = TokenType.ALT;
                    state = A_LAYER;
                }
                break;
            case C_LAYER + OTHER:
            case A_LAYER + OTHER:
                context.buf.append(c);
                break;
            case A_LAYER + VBAR:
                String alt = claimBuffer(context.buf);
                context.alternatives.add(alt);
                break;
            case C_LAYER + COMMA:
                if (0 == context.buf.length()) {
                    throw new ParseException("layer name cannot be empty", position);
                }
                String closeLayer = claimBuffer(context.buf);
                context.layers.add(closeLayer);
                break;
            case C_LAYER + CLOSE_SQ:
                if (0 == context.buf.length()) {
                    throw new ParseException("layer name cannot be empty", position);
                }
                context.layers.add(claimBuffer(context.buf));
                ret = buildToken(context); // build the open tag
                state = OUT;
                break;
            case A_LAYER + CLOSE_ANG:
                if (0 != context.buf.length()) {
                    throw new ParseException("Alternate must end with |>", position);
                }
                ret = buildToken(context); // build the open tag
                state = OUT;
                break;

            case O_OR_S + COMMA:
            case C_OR_A + COMMA:
                throw new ParseException("Comma is not allowed in a tag name", position);

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
                context.value = claimBuffer(context.buf);
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
