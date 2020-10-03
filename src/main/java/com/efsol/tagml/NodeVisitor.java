package com.efsol.tagml;

public interface NodeVisitor {
	/** return true to continue, false to stop **/
	boolean visit(Node node); 
}
