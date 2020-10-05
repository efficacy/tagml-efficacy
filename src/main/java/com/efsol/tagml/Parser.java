package com.efsol.tagml;

import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.OpenToken;
import com.efsol.tagml.lex.TextToken;
import com.efsol.tagml.lex.Token;
import com.efsol.tagml.model.Chunk;
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
		parse(lexer);
		Document ret = factory.createDocument();
		return ret;
	}

	public void parse(Lexer lexer) throws IOException {
		factory.reset();
		ParseContext context = new ParseContext(factory);

		for(;;) {
			Token token = lexer.next();
			log("Parser: got lexer token: " + token);
			if (null == token) {
				break;
			}

			switch(token.getType()) {
			case TEXT:
				log("Parser: it's a text token: " + token);
				context.append(((TextToken)token).getText());
				context.setPosition(token.getPosition());
				factory.addChunk(context.swap());
				break;
			case OPEN:
				log("Parser: it's an open token: " + token);
				OpenToken ot = (OpenToken)token;
				context.addTag(ot.getName(), ot.getLayer(), ot.getPosition());
				log("after open tag ctx=" + context);
				break;
			case CLOSE:
				log("Parser: it's a close token: " + token);
				CloseToken ct = (CloseToken)token;
				context.removeTag(ct.getName(), ct.getLayer(), ct.getPosition());
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

		if (context.isIncomplete()) {
			log("at end, buffer is incomplete");
			Chunk chunk = context.swap();
			log("completed incomplete chunk: " + chunk);
			factory.addChunk(chunk);
		}
	}

	void log(String s) {
		if (verbose) System.out.println("Parser::" + s);
	}
}
