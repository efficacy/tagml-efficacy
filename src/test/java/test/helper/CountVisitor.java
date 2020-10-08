package test.helper;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;

public class CountVisitor implements ChunkVisitor {
    public int count = 0;

    @Override
    public Object visit(Chunk chunk) {
        ++count;
        return null;
    }
}