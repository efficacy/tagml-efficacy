package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.Layer;
import com.efsol.tagml.Node;
import com.efsol.tagml.NodeVisitor;
import com.efsol.tagml.TagmlDocument;
import com.efsol.tagml.TagmlParser;

class CountVisitor implements NodeVisitor {
	public int count = 0;

	@Override
	public boolean visit(Node node) {
		++count;
		return true;
	}
}

class ParserTest {

	void assertLayerCount(TagmlDocument doc, int expected) {
		Collection<Layer> layers = doc.getlayers();
		assertEquals(expected, layers.size());
	}

	void assertNodeCount(int expected, Layer layer) {
		CountVisitor visitor = new CountVisitor();
		layer.walk(visitor);
		assertEquals(expected, visitor.count);
	}

	@Test
	void testEmpty() {
		TagmlParser parser = new TagmlParser();
		TagmlDocument doc = parser.parse("");

		assertNotNull(doc);
		assertLayerCount(doc, 1);
		assertNodeCount(0, doc.getBaseLayer());
	}

	@Test
	void testPlain() {
		TagmlParser parser = new TagmlParser();
		TagmlDocument doc = parser.parse("John");

		assertNotNull(doc);
		assertLayerCount(doc, 1);
		assertNodeCount(1, doc.getBaseLayer());
	}

}
