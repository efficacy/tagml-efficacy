package com.efsol.tagml.model.dag;

import java.util.ArrayList;
import java.util.Collection;

import com.efsol.tagml.model.Chunk;
import com.efsol.tagml.model.Node;
import com.efsol.tagml.model.Tag;

public class DagNode implements Node {
    private DagLayer layer;
    private Collection<Tag> tags;
    protected Collection<Chunk> prev;
    protected Collection<Chunk> next;

    private static Collection<Chunk> wrap(Chunk chunk) {
        Collection<Chunk> list = new ArrayList<>();
        list.add(chunk);
        return list;
    }

    public DagNode(final DagLayer layer, final Collection<Tag> tags, final Collection<Chunk> prev, final Collection<Chunk> next) {
        this.layer = layer;
        this.tags = tags;
        this.prev = prev;
        this.next = next;
    }

    public DagNode(final DagLayer layer, final Collection<Tag> tags, final Chunk prev, final Chunk next) {
        this(layer, tags, wrap(prev), wrap(next));
    }

    public DagNode(final DagLayer layer, final Collection<Tag> tags) {
        this(layer, tags, (Collection<Chunk>) null, (Collection<Chunk>) null);
    }

    @Override
    public void setPrevious(Chunk chunk) {
        if (null == prev) {
            prev = wrap(chunk);
        } else {
            prev.add(chunk);
        }
    }

    @Override
    public void setNext(Chunk chunk) {
        if (null == next) {
            next = wrap(chunk);
        } else {
            next.add(chunk);
        }
    }

    public DagLayer getHead() {
        return layer;
    }

    @Override
    public Collection<Chunk> getPrevious() {
        return prev;
    }

    @Override
    public Collection<Chunk> getNext() {
        return next;
    }

    @Override
    public Collection<Tag> getTags() {
        return tags;
    }

    @Override
    public String getLayerName() {
        return layer.getName();
    }

    @Override
    public String toString() {
        String name = null == layer ? "null" : layer.getName();
        return "DAGNode(" + name + ") tags=" + tags + ")";
    }
}
