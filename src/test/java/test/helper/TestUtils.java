package test.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkSequence;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.DocumentFactory;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.model.dag.DagFactory;
import com.efsol.tagml.parser.Parser;

public class TestUtils {
    public static void assertNodeCount(int expected, ChunkSequence sequence) {
        CountVisitor visitor = new CountVisitor();
        sequence.walk(visitor);
        assertEquals(expected, visitor.count);
    }

    public static void assertChunkHasTag(Chunk chunk, String layerName, String tagName) {
        Map<String, Node> layers = chunk.getLayers();
        Node node = layers.get(layerName);
        assertNotNull(node, "can't find tag " + tagName + " in layer " + layerName + " " + layers);
        for (Tag tag : node.getTags()) {
            if (tagName.equals(tag.name)) {
                return;
            }
        }
        fail("tag " + tagName + " not found in layer " + layerName);
    }

    public static Document parse(String input) throws IOException {
            DocumentFactory factory = new DagFactory();
            Parser parser = new Parser(factory);
            StringReader reader = new StringReader(input);
            Document document = parser.parse(reader);
    //		System.out.println("parsed(" + input + ") to " + document);
            return document;
        }
}
