package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.DocumentFilter;
import com.efsol.tagml.Layer;
import com.efsol.tagml.Node;
import com.efsol.tagml.NodeVisitor;
import com.efsol.tagml.ParseContext;
import com.efsol.tagml.Tag;
import com.efsol.tagml.TagmlDocument;
import com.efsol.tagml.TagmlParser;
import com.efsol.tagml.lex.Lexer;
import com.efsol.util.Utils;

class CountVisitor implements NodeVisitor {
	public int count = 0;

	@Override
	public boolean visit(Node node) {
		++count;
		return true;
	}
}

class GetVisitor implements NodeVisitor {
	private int target;
	public int count = 0;
	public Node found;

	public GetVisitor(int target) {
		this.target = target;
	}

	@Override
	public boolean visit(Node node) {
		if (count == target) {
			found = node;
			return false;
		}
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

	void assertNodeHasTag(Node node, String layerName, String tagName) {
		Map<String, Collection<Tag>> layers = node.getLayers();
		Collection<Tag> layer = layers.get(layerName);
		assertNotNull(layer, "can't find tag " + tagName + " in missing layer " + layerName);
		for (Tag tag : layer) {
			if (tagName.equals(tag.type)) {
				return;
			}
		}
		fail("tag " + tagName +" not found in layer " +layerName);
	}

	class SingleLayerFilter implements DocumentFilter {
		private final String layerName;

		public SingleLayerFilter(String layerName) {
			this.layerName = layerName;
		}

		@Override
		public boolean accept(Node node) {
//			System.out.println("SingleLayerFilter.accept(" + layerName + ") considering " + node);
			Map<String, Collection<Tag>> layers = node.getLayers();
			return (null == layerName && layers.isEmpty()) || layers.containsKey(layerName);
		}

		@Override
		public boolean acceptLayer(String layer) {
//			System.out.println("SingleLayerFilter.acceptlayer(" + layerName + ") considering " + layer);
			return Utils.same(layer, layerName);
		}
	}

	void assertPlainLayer(String expected, TagmlDocument document, String layerName) {
		String text = document.spoolAsText(new SingleLayerFilter(layerName));
		assertEquals(expected, text);
	}

	void assertAnnotatedLayer(String expected, TagmlDocument document, String layerName) {
		String text = document.spoolAsMarkup(new SingleLayerFilter(layerName));
		assertEquals(expected, text);
	}

	Node get(Layer layer, int index) {
		GetVisitor visitor = new GetVisitor(index);
		layer.walk(visitor);
		return visitor.found;
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
		assertLayerCount(0, doc);
		assertNodeCount(0, doc.getGlobalLayer());
	}

	@Test
	void testPlainTextGlobal() throws IOException {
		TagmlDocument doc = parse("John");

		assertNotNull(doc);
		assertNodeCount(1, doc.getGlobalLayer());
	}

	@Test
	void testStartEnd() throws IOException {
		Lexer.verbose = false;
		TagmlParser.verbose = false;
		ParseContext.verbose = false;
		TagmlDocument doc = parse("[A>John<A]");

		assertNotNull(doc);
		Layer global = doc.getGlobalLayer();
//		global.dump();
		assertNodeCount(1, global);
		Node node = get(global,0);
		assertNotNull(node);
		assertEquals("John", node.getValue());
		assertNodeHasTag(node, null, "A");

		assertPlainLayer("John", doc, null);
		assertPlainLayer("", doc, "X");

		assertAnnotatedLayer("[A>John<A]", doc, null);
		assertAnnotatedLayer("", doc, "X");
	}

	@Test
	void testOverlap() throws IOException {
		TagmlDocument doc = parse("Stuart[A>John[B>Paul<A]George<B]Ringo");

		assertNotNull(doc);
		Layer global = doc.getGlobalLayer();
		assertNodeCount(5, global);

		assertPlainLayer("StuartJohnPaulGeorgeRingo", doc, null);
		assertPlainLayer("", doc, "X");

		assertAnnotatedLayer("Stuart[A>John[B>Paul<A]George<B]Ringo", doc, null);
		assertAnnotatedLayer("", doc, "X");
	}

	@Test
	void testLayers() throws IOException {
		TagmlDocument doc = parse("Stuart[A|+f>John[B>Paul<A|f]George<B]Ringo");

		assertNotNull(doc);
		Layer global = doc.getGlobalLayer();
		assertNodeCount(5, global);

		assertPlainLayer("JohnPaul", doc, "f");
		assertPlainLayer("StuartPaulGeorgeRingo", doc, null);

		assertAnnotatedLayer("[A|f>JohnPaul<A|f]", doc, "f");
		assertAnnotatedLayer("Stuart[B>PaulGeorge<B]Ringo", doc, null);
	}
}
