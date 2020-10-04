package com.efsol.tagml;

import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.lex.Token;
import com.efsol.tagml.lex.CloseToken;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.OpenToken;
import com.efsol.tagml.lex.TextToken;

public class TagmlParser {
	public static boolean verbose = false;
	public TagmlDocument parse(Reader input) throws IOException {
		Lexer lexer = new Lexer(input);
		return parse(lexer);
	}

	public TagmlDocument parse(Lexer lexer) throws IOException {
		TagmlDocument ret = new TagmlDocument();
		ParseContext context = new ParseContext(ret);

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
				ret.addNode(context.swap());
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
			Node node = context.swap();
			log("completed incomplete node: " + node);
			ret.addNode(node);
		}
		log("Parser returning: " + ret);
		return ret;
	}

	void log(String s) {
		if (verbose) System.out.println("Parser::" + s);
	}
}
