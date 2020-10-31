package com.efsol.tagml.parser;

import java.util.Collection;

import com.efsol.tagml.model.Position;
import com.efsol.tagml.model.helper.LayerContext;

public interface ParseContext {
    public Collection<LayerContext> getLayers();
    public void addText(String text, Position position);
    public void addTag(String name, Collection<String> layers, Position position);
    public void removeTag(String name, Collection<String> layers, Position position) throws ParseException;
    public boolean isIncomplete();
    public void setPosition(Position position);
    public void enforceConsistency() throws ParseException;
    public void tail();
}
