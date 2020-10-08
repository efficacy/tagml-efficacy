package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Document;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.Tag;
import com.efsol.tagml.model.dag.DagChunk;
import com.efsol.tagml.model.dag.DagFactory;

import test.helper.TestUtils;

class DocumentTest {

    @Test
    void testEmpty() {
        DagFactory factory = new DagFactory();
        Document doc = factory.createDocument();
        TestUtils.assertNodeCount(0, doc);

        // check that iterator works, too
        Iterator<Chunk> it = doc.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    void testOneChunkWithNoTags() {
        DagFactory factory = new DagFactory();
        Map<String, Node> nodes = new HashMap<>();
        Chunk chunk = new DagChunk("hello", new Position(1, 1), nodes);
        factory.addChunk(chunk);

        Document doc = factory.createDocument();
        TestUtils.assertNodeCount(1, doc);
//        System.out.println(doc);
    }

    @Test
    void testOneChunkWithTagButNoLayer() {
        DagFactory factory = new DagFactory();
        Node node = factory.createNode(null, Arrays.asList(new Tag("A", null, null, null, null)));
        Map<String, Node> nodes = new HashMap<>();
        nodes.put(null, node);
        Chunk chunk = new DagChunk("hello", new Position(1, 1), nodes);
        factory.addChunk(chunk);

        Document doc = factory.createDocument();
        TestUtils.assertNodeCount(1, doc);
//        System.out.println(doc);
    }

    @Test
    void testOneChunkWithTagAndLayer() {
        DagFactory factory = new DagFactory();
        Node node = factory.createNode("f", Arrays.asList(new Tag("A", "f", null, null, null)));
        Map<String, Node> nodes = new HashMap<>();
        nodes.put("f", node);
        Chunk chunk = new DagChunk("hello", new Position(1, 1), nodes);
        factory.addChunk(chunk);

        Document doc = factory.createDocument();
        TestUtils.assertNodeCount(1, doc);
//        System.out.println(doc);

        // check that iterator works, too
        Iterator<Chunk> it = doc.iterator();
        assertTrue(it.hasNext());
        Chunk c = it.next();
        assertNotNull(c);
        assertFalse(it.hasNext());
    }

}
