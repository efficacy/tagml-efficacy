package com.efsol.tagml;

import java.io.IOException;
import java.io.Reader;

import com.efsol.tagml.lex.Token;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.lex.TextToken;

public class TagmlParser {
	public static boolean verbose = false;
	public TagmlDocument parse(Reader input) throws IOException {
		Lexer lexer = new Lexer(input);
		return parse(lexer);
	}

	public TagmlDocument parse(Lexer lexer) throws IOException {
		TagmlDocument ret = new TagmlDocument();
		NodeBuffer buffer = new NodeBuffer(lexer.getPosition());
		for(;;) {
			Token token = lexer.next();
			log("Parser: got token: " + token);
			if (null == token) {
				break;
			}

			switch(token.getType()) {
			case NONE:
				// no token, so do nothing
				break;
			case TEXT:
				log("Parser: it's a text token: " + token);
				buffer.append(((TextToken)token).getText());
				break;
			case OPEN:
				ret.addNode(buffer.swap(lexer.getPosition()));
				// TODO update tag/layer context
				break;
			case CLOSE:
				ret.addNode(buffer.swap(lexer.getPosition()));
				// TODO update tag/layer context
				break;
			case SINGLE:
				ret.addNode(buffer.swap(lexer.getPosition()));
				// TODO create and save a meta node
				break;
			case ALT:
				ret.addNode(buffer.swap(lexer.getPosition()));
				// TODO create and save a meta node
				break;
			}
		}

		if (buffer.isIncomplete()) {
			log("at end, buffer is incomplete");
			Node node = buffer.swap(lexer.getPosition());
			log("created incomplete node: " + node);
			ret.addNode(node);
		}
		log("Parser returning: " + ret);
		return ret;
	}

	void log(String s) {
		if (verbose) System.out.println(s);
	}
}
