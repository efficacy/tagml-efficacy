package com.efsol.tagml.lex;

import java.util.Collection;

import com.efsol.tagml.model.Position;

public abstract class TagToken extends AbstractToken {
    protected final String name;
    protected final Collection<String> layers;

    public TagToken(TokenType type, String name, Collection<String> layers, Position position) {
        super(type, position);
        this.name = name;
        this.layers = layers;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getLayers() {
        return layers;
    }
}
