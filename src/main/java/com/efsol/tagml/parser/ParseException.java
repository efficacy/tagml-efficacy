package com.efsol.tagml.parser;

import java.io.IOException;

import com.efsol.tagml.model.Position;

@SuppressWarnings("serial")
public class ParseException extends IOException {
    public final Position position;

    public ParseException(String message, Position position) {
        super(message + " at " + position.snapshot());
        this.position = position.snapshot();
    }
}
