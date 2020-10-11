package com.efsol.tagml.model;

import java.util.Collection;

import com.efsol.util.Utils;

/** return null to continue, non-null to stop and return **/
public interface ChunkVisitor {
    Object visit(Chunk chunk);

    default Chunk selectPath(Collection<Chunk> chunks) {
        return Utils.justPickOne(chunks); // Override in "real" visitors
    }
}
