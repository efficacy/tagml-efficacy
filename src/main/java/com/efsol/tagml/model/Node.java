package com.efsol.tagml.model;

import java.util.Collection;

public interface Node {
    String getLayerName();

    Collection<Tag> getTags();
    Collection<Chunk> getNext();
    Collection<Chunk> getPrevious();

    void setPrevious(Chunk prev);
    void setNext(Chunk next);
}
