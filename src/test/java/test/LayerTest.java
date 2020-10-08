package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Layer;
import com.efsol.tagml.model.dag.DagFactory;
import com.efsol.tagml.model.dag.DagLayer;

import test.helper.TestUtils;

class LayerTest {

    @Test
    void testEmpty() {
        DagFactory factory = new DagFactory();
        Layer layer = new DagLayer("test", factory);
        TestUtils.assertNodeCount(0, layer);

        // check that iterator works, too
        Iterator<Chunk> it = layer.iterator();
        assertFalse(it.hasNext());
    }

}
