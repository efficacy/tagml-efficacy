package test.helper;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.ChunkVisitor;

public class GetVisitor implements ChunkVisitor {
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