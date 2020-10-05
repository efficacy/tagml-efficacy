package com.efsol.tagml.model;

/** return null to continue, non-null to stop and return **/
public interface ChunkVisitor {
	Object visit(Chunk chunk);
}
