package com.efsol.tagml;

import java.io.Reader;

public class TagmlParser {
	public TagmlDocument parse(Reader input) {
		Lexer lexer = new Lexer(input);
		return parse(lexer);
	}

	public TagmlDocument parse(Lexer lexer) {
		TagmlDocument ret = new TagmlDocument();
		while (lexer.hasNext()) {
			LexToken token = lexer.next();
			switch(token.getType()) {
			case text:
				// TODO
				break;
			case open:
				// TODO
				break;
			case close:
				// TODO
				break;
			case end:
				// TODO
				break;
			}
		}
		return ret;
	}

}
