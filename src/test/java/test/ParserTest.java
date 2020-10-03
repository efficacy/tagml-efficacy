package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.Layer;
import com.efsol.tagml.Node;
import com.efsol.tagml.NodeVisitor;
import com.efsol.tagml.TagmlDocument;
import com.efsol.tagml.TagmlParser;
import com.efsol.tagml.lex.Lexer;

class CountVisitor implements NodeVisitor {
	public int count = 0;

	@Override
	public boolean visit(Node node) {
		++count;
		return true;
	}
}

class ParserTest {

	void assertLayerCount(int expected, TagmlDocument doc) {
		Collection<Layer> layers = doc.getlayers();
		assertEquals(expected, layers.size());
	}

	void assertNodeCount(int expected, Layer layer) {
		CountVisitor visitor = new CountVisitor();
		layer.walk(visitor);
		assertEquals(expected, visitor.count);
	}

	TagmlDocument parse(String input) throws IOException {
		TagmlParser parser = new TagmlParser();
		StringReader reader = new StringReader(input);
		return parser.parse(reader);
	}

	@Test
	void testEmpty() throws IOException {
		TagmlDocument doc = parse("");

		assertNotNull(doc);
		assertLayerCount(1, doc);
		assertNodeCount(0, doc.getGlobalLayer());
	}

	@Test
	void testPlainTextGlobal() throws IOException {
		Lexer.verbose = true;
		TagmlParser.verbose = true;
		TagmlDocument doc = parse("John");

		assertNotNull(doc);
//		assertLayerCount(2, doc); // global and base
		assertNodeCount(1, doc.getGlobalLayer());
	}

}
