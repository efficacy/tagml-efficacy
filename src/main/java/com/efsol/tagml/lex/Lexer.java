package com.efsol.tagml.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.efsol.tagml.model.Position;
import com.efsol.tagml.parser.ParseException;

public class Lexer {
    public static boolean verbose = false;

    /** states **/
    enum State {
        OUT, O_OR_S, OS_LAYER, C_OR_A, C_LAYER, A_LAYER
    }

    /** character types **/
    enum Ctype {
        OPEN_SQ, OPEN_ANG, CLOSE_SQ, CLOSE_ANG, NAME, VBAR, PLUS, MINUS, QUERY, COMMA, WHITESPACE, OTHER
    }

    private TokenContext context;
    private Reader input;
    private Position position;
    private boolean esc;
    private State state;

    public Lexer(Reader input) {
        this.context = new TokenContext();
        this.input = input;
        this.position = new Position();
        this.esc = false;
        this.state = State.OUT;
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

    public static Ctype charType(int c) {
        switch (c) {
        case '[':
            return Ctype.OPEN_SQ;
        case ']':
            return Ctype.CLOSE_SQ;
        case '<':
            return Ctype.OPEN_ANG;
        case '>':
            return Ctype.CLOSE_ANG;
        case '|':
            return Ctype.VBAR;
        case '+':
            return Ctype.PLUS;
        case '-':
            return Ctype.MINUS;
        case '?':
            return Ctype.QUERY;
        case ',':
            return Ctype.COMMA;
        case '_':
            return Ctype.NAME;
        default:
            if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                return Ctype.NAME;
            }
            if (Character.isWhitespace(c)) {
                return Ctype.WHITESPACE;
            }
            return Ctype.OTHER;
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
            ret = new OpenToken(context.name, snapshotLayers(context.layers), context.position);
            log("biulding open created tag=" + ret);
            break;
        case CLOSE:
            log("biulding close ctx=" + context);
            ret = new CloseToken(context.name, snapshotLayers(context.layers), context.position);
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
            Ctype ctype = esc ? Ctype.OTHER : charType(i);
            char c = (char) i;
            log("state=" + state + " charType(" + c + ")=" + ctype);
            switch (state) {
            case OUT:
                switch (ctype) {
                case OPEN_ANG:
                    ret = flushText();
                    context.setPosition(position.snapshot());
                    context.type = TokenType.CLOSE;
                    log("entering close or alt, context pos=" + context.position);
                    state = State.C_OR_A;
                    break;
                case OPEN_SQ:
                    ret = flushText();
                    context.setPosition(position.snapshot());
                    context.type = TokenType.OPEN;
                    log("entering open or single, context pos=" + context.position);
                    state = State.O_OR_S;
                    break;
                case CLOSE_ANG:
                case CLOSE_SQ:
                case COMMA:
                case MINUS:
                case PLUS:
                case QUERY:
                case VBAR:
                case NAME:
                case WHITESPACE:
                case OTHER:
                    if (null == context.type) {
                        context.type = TokenType.TEXT;
                        context.setPosition(position.snapshot());
//                log("starting text at " + context.position);
                    }
                    context.buf.append(c);
                    log(" token type=" + context.type + " appended(" + c + ") value=" + context.value);
                    break;
                }
                break;
            case O_OR_S:
                switch (ctype) {
                case WHITESPACE:
                    // ignore whitespace in tags
                    break;
                case CLOSE_ANG:
                    if (0 == context.buf.length()) {
                        throw new ParseException("open tag name cannot be empty", position);
                    }
                    context.name = claimBuffer(context.buf);
                    ret = buildToken(context); // build the open tag
                    state = State.OUT;
                    break;
                case CLOSE_SQ:
                    if (0 == context.buf.length()) {
                        throw new ParseException("open tag name cannot be empty", position);
                    }
                    context.name = claimBuffer(context.buf);
                    context.type = TokenType.SINGLE;
                    ret = buildToken(context); // build the open tag
                    state = State.OUT;
                case NAME:
                    context.buf.append(c);
                    break;
                case VBAR:
                    if (0 == context.buf.length()) {
                        throw new ParseException("open tag name cannot be empty", position);
                    }
                    context.name = claimBuffer(context.buf);
                    state = State.OS_LAYER;
                    break;

                case PLUS:
                case MINUS:
                    // TODO tag continuation

                case OTHER:
                case COMMA:
                case OPEN_ANG:
                case OPEN_SQ:
                case QUERY:
                    throw new ParseException("unexpected character " + c + " in tag name", position);
                }
                break;
            case C_OR_A:
                switch (ctype) {
                case WHITESPACE:
                    // ignore whitespace in tags
                    break;
                case CLOSE_SQ:
                    log("building close token, context pos=" + context.position);
                    if (0 == context.buf.length()) {
                        throw new ParseException("close tag name cannot be empty", position);
                    }
                    context.name = claimBuffer(context.buf);
                    ret = buildToken(context); // build the close tag
                    state = State.OUT;
                    break;
                case NAME:
                    context.buf.append(c);
                    break;
                case VBAR:
                    if (context.buf.length() > 0) {
                        context.name = claimBuffer(context.buf);
                        context.type = TokenType.CLOSE;
                        state = State.C_LAYER;
                    } else {
                        context.type = TokenType.ALT;
                        state = State.A_LAYER;
                    }
                    break;

                case PLUS:
                case MINUS:
                    // TODO tag continuation

                case CLOSE_ANG:
                case QUERY:
                case OPEN_ANG:
                case OPEN_SQ:
                case COMMA:
                case OTHER:
                    throw new ParseException("unexpected character " + c + " in tag name", position);
                }
                break;
            case OS_LAYER:
                switch (ctype) {
                case WHITESPACE:
                    // ignore whitespace in tags
                    break;
                case CLOSE_ANG:
                    if (0 == context.buf.length()) {
                        throw new ParseException("layer name cannot be empty", position);
                    }
                    context.layers.add(claimBuffer(context.buf));
                    ret = buildToken(context); // build the open tag
                    state = State.OUT;
                    break;
                case CLOSE_SQ:
                    if (0 == context.buf.length()) {
                        throw new ParseException("layer name cannot be empty", position);
                    }
                    context.type = TokenType.SINGLE;
                    ret = buildToken(context); // build the singleton tag
                    state = State.OUT;
                    break;
                case COMMA:
                    if (0 == context.buf.length()) {
                        throw new ParseException("layer name cannot be empty", position);
                    }
                    String openLayer = claimBuffer(context.buf);
                    context.layers.add(openLayer);
                    break;
                case PLUS:
                    // ignore plus and auto-create layers for now
                    // TODO reject if not at start of name
                    break;
                case NAME:
                    context.buf.append(c);
                    break;
                case OPEN_ANG:
                case OPEN_SQ:
                case QUERY:
                case VBAR:
                case MINUS:
                case OTHER:
                    throw new ParseException("unexpected character " + c + " in layer name", position);
                }
                break;
            case C_LAYER:
                switch (ctype) {
                case WHITESPACE:
                    // ignore whitespace in tags
                    break;
                case CLOSE_SQ:
                    if (0 == context.buf.length()) {
                        throw new ParseException("layer name cannot be empty", position);
                    }
                    context.layers.add(claimBuffer(context.buf));
                    ret = buildToken(context); // build the open tag
                    state = State.OUT;
                    break;
                case COMMA:
                    if (0 == context.buf.length()) {
                        throw new ParseException("layer name cannot be empty", position);
                    }
                    String closeLayer = claimBuffer(context.buf);
                    context.layers.add(closeLayer);
                    break;
                case NAME:
                    context.buf.append(c);
                    break;
                case OPEN_ANG:
                case OPEN_SQ:
                case CLOSE_ANG:
                case MINUS:
                case PLUS:
                case QUERY:
                case VBAR:
                case OTHER:
                    throw new ParseException("unexpected character " + c + " in layer name", position);
                }
                break;
            case A_LAYER:
                switch (ctype) {
                case CLOSE_ANG:
                    if (0 != context.buf.length()) {
                        throw new ParseException("Alternate must end with |>", position);
                    }
                    ret = buildToken(context); // build the open tag
                    state = State.OUT;
                    break;
                case VBAR:
                    String alt = claimBuffer(context.buf);
                    context.alternatives.add(alt);
                    break;
                case WHITESPACE:
                case CLOSE_SQ:
                case COMMA:
                case MINUS:
                case OPEN_ANG:
                case OPEN_SQ:
                case PLUS:
                case QUERY:
                case NAME:
                case OTHER:
                    // just accumulate text for now
                    // TODO decide how to parse and store alternate document fragments
                    context.buf.append(c);
                    break;
                }
                break;

            default:
                throw new ParseException("unexpected character " + c + " in state " + state, position);
            }
            log("after switch, state=" + state + " ctx=" + context);
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

}
