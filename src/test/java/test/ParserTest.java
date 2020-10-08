package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.ParseContext;
import com.efsol.tagml.Parser;
import com.efsol.tagml.lex.Lexer;
import com.efsol.tagml.markup.Markup;
import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.dag.DagFactory;

import test.helper.GetVisitor;
import test.helper.SingleLayerFilter;
import test.helper.TestUtils;

public class ParserTest {

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
        return (Chunk) layer.walk(visitor);
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
        TestUtils.assertNodeCount(0, doc);
    }

    @Test
    void testPlainTextGlobal() throws IOException {
        Document doc = parse("John");

        assertNotNull(doc);
        TestUtils.assertNodeCount(1, doc);
    }

    @Test
    void testStartEnd() throws IOException {
        Document doc = parse("[A>John<A]");

        assertNotNull(doc);
        Layer global = doc.getGlobalLayer();
//		global.dump();
        TestUtils.assertNodeCount(1, global);
        Chunk chunk = get(global, 0);
        assertNotNull(chunk);
        assertEquals("John", chunk.getValue());
        TestUtils.assertChunkHasTag(chunk, null, "A");

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
        TestUtils.assertNodeCount(5, global);

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
        TestUtils.assertNodeCount(5, global);

        assertPlainLayer("JohnPaul", doc, "f");
        assertPlainLayer("StuartPaulGeorgeRingo", doc, null);

        assertAnnotatedLayer("[A|f>JohnPaul<A|f]", doc, "f");
        assertAnnotatedLayer("Stuart[B>PaulGeorge<B]Ringo", doc, null);
    }
}
