package com.efsol.tagml.lex;

import java.util.Collection;

import com.efsol.tagml.model.Position;

public class OpenToken extends TagToken {
    public OpenToken(String name, Collection<String> layers, Position position) {
        super(TokenType.OPEN, name, layers, position);
    }

    @Override
    public String toString() {
        return "OPEN[" + name + "," + layers + "] at " + getPosition();
    }
}
