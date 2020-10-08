package test.helper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkSequence;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;

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
}
