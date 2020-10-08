package com.efsol.tagml.lex;

import java.util.Collection;

import com.efsol.tagml.model.Position;

public class CloseToken extends TagToken {
    public CloseToken(String name, Collection<String> layers, Position position) {
        super(TokenType.CLOSE, name, layers, position);
    }

    @Override
    public String toString() {
        return "CLOSE[" + name + "," + layers + "] at " + getPosition();
    }
}
