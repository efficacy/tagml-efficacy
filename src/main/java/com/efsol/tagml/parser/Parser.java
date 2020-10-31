package com.efsol.tagml.parser;

import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.OpenToken;
import com.efsol.tagml.lex.TextToken;
import com.efsol.tagml.lex.Token;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;

public class Parser {
    public static boolean verbose = false;
    private DocumentFactory factory;

    public Parser(DocumentFactory factory) {
        this.factory = factory;
    }

    public Document parse(Reader input) throws IOException {
        Lexer lexer = new Lexer(input);
        ParseContext context = factory.createContext();
        parse(lexer, context);
        Document ret = factory.createDocument();
        return ret;
    }

    public void parse(Lexer lexer, ParseContext context) throws IOException {
        factory.reset();

        for (;;) {
            Token token = lexer.next();
            log("Parser: got lexer token: " + token);
            if (null == token) {
                break;
            }

            switch (token.getType()) {
            case TEXT:
                log("Parser: it's a text token: " + token);
                context.addText(((TextToken) token).getText(), token.getPosition());
                break;
            case OPEN:
                log("Parser: it's an open token: " + token);
                OpenToken ot = (OpenToken) token;
                context.addTag(ot.getName(), ot.getLayers(), ot.getPosition());
                log("after open tag ctx=" + context);
                break;
            case CLOSE:
                log("Parser: it's a close token: " + token);
                CloseToken ct = (CloseToken) token;
                context.removeTag(ct.getName(), ct.getLayers(), ct.getPosition());
                log("after close tag ctx=" + context);
                break;
            case SINGLE:
                log("Parser: it's a singleton token: " + token);
                // TODO create and save a meta node
                break;
            case ALT:
                log("Parser: it's an alternative token: " + token);
                // TODO create and save a meta node
                break;
            }
        }

        context.tail();

        context.enforceConsistency();
    }

    static void log(String s) {
        if (verbose)
            System.out.println("Parser::" + s);
    }
}
