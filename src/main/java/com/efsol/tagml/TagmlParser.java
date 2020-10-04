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
			case NONE:
				// no token, so do nothing
				break;
			case TEXT:
				log("Parser: it's a text token: " + token);
				context.append(((TextToken)token).getText());
				context.setPosition(token.getPosition());
				break;
			case OPEN:
				log("Parser: it's an open token: " + token);
				// aggregate any pending text nodes
				log("pending text node ctx=" + context);
				ret.addNode(context.swap());
				OpenToken ot = (OpenToken)token;
				context.addTag(ot.getName(), null, ot.getPosition());
				// TODO update layer context
				log("after open tag ctx=" + context);
				break;
			case CLOSE:
				log("Parser: it's a close token: " + token);
				// aggregate any pending text nodes
				log("pending text node ctx=" + context);
				ret.addNode(context.swap());
				CloseToken ct = (CloseToken)token;
				context.removeTag(ct.getName(), null, ct.getPosition());
				// TODO update layer context
				log("after close tag ctx=" + context);
				break;
			case SINGLE:
				log("Parser: it's a singleton token: " + token);
				// aggregate any pending text nodes
				ret.addNode(context.swap());
				// TODO create and save a meta node
				break;
			case ALT:
				log("Parser: it's an alternative token: " + token);
				// aggregate any pending text nodes
				ret.addNode(context.swap());
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
