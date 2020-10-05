package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.ParseContext;
import com.efsol.tagml.Parser;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.markup.Markup;
import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.DocumentFilter;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.model.dag.DagFactory;
import com.efsol.util.Utils;

class CountVisitor implements ChunkVisitor {
	public int count = 0;

	@Override
	public Object visit(Chunk chunk) {
		++count;
		return null;
	}
}

class GetVisitor implements ChunkVisitor {
	private int target;
	public int count = 0;

	public GetVisitor(int target) {
		this.target = target;
	}

	@Override
	public Object visit(Chunk chunk) {
		if (count == target) {
			return chunk;
		}
		++count;
		return null;
	}
}

class ParserTest {

	void assertNodeCount(int expected, Layer layer) {
		CountVisitor visitor = new CountVisitor();
		layer.walk(visitor);
		assertEquals(expected, visitor.count);
	}

	void assertChunkHasTag(Chunk chunk, String layerName, String tagName) {
		Map<String, Node> layers = chunk.getLayers();
		Node node = layers.get(layerName);
		assertNotNull(node, "can't find tag " + tagName + " in layer " + layerName + " " + layers);
		for (Tag tag : node.getTags()) {
			if (tagName.equals(tag.name)) {
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
		public boolean accept(Chunk chunk) {
			return chunk.isOnLayer(layerName);
		}

		@Override
		public boolean acceptLayer(String layer) {
			return Utils.same(layer, layerName);
		}
	}

	void assertPlainLayer(String expected, Document document, String layerName) {
		String text = Markup.spoolAsText(document, new SingleLayerFilter(layerName));
		assertEquals(expected, text);
	}

	void assertAnnotatedLayer(String expected, Document document, String layerName) {
		String text = Markup.spoolAsMarkup(document, new SingleLayerFilter(layerName));
		assertEquals(expected, text);
	}

	Chunk get(Layer layer, int index) {
		GetVisitor visitor = new GetVisitor(index);
		return (Chunk)layer.walk(visitor);
	}

	Document parse(String input) throws IOException {
		DocumentFactory factory = new DagFactory();
		Parser parser = new Parser(factory);
		StringReader reader = new StringReader(input);
		Document document = parser.parse(reader);
//		System.out.println("parsed(" + input + ") to " + document);
		return document;
	}

	@Test
	void testEmpty() throws IOException {
		Lexer.verbose = false;
		Parser.verbose = false;
		ParseContext.verbose = false;
		Document doc = parse("");

		assertNotNull(doc);
		assertNodeCount(0, doc.getGlobalLayer());
	}

	@Test
	void testPlainTextGlobal() throws IOException {
		Document doc = parse("John");

		assertNotNull(doc);
		assertNodeCount(1, doc.getGlobalLayer());
	}

	@Test
	void testStartEnd() throws IOException {
		Document doc = parse("[A>John<A]");

		assertNotNull(doc);
		Layer global = doc.getGlobalLayer();
//		global.dump();
		assertNodeCount(1, global);
		Chunk chunk = get(global,0);
		assertNotNull(chunk);
		assertEquals("John", chunk.getValue());
		assertChunkHasTag(chunk, null, "A");

		assertPlainLayer("John", doc, null);
		assertPlainLayer("", doc, "X");

		assertAnnotatedLayer("[A>John<A]", doc, null);
		assertAnnotatedLayer("", doc, "X");
	}

	@Test
	void testOverlap() throws IOException {
		Document doc = parse("Stuart[A>John[B>Paul<A]George<B]Ringo");

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
		Document doc = parse("Stuart[A|+f>John[B>Paul<A|f]George<B]Ringo");

		assertNotNull(doc);
		Layer global = doc.getGlobalLayer();
		assertNodeCount(5, global);

		assertPlainLayer("JohnPaul", doc, "f");
		assertPlainLayer("StuartPaulGeorgeRingo", doc, null);

		assertAnnotatedLayer("[A|f>JohnPaul<A|f]", doc, "f");
		assertAnnotatedLayer("Stuart[B>PaulGeorge<B]Ringo", doc, null);
	}
}
