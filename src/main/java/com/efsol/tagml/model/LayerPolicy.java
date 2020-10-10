package com.efsol.tagml.model;

public interface LayerPolicy {
    boolean followLayer(String layerName);

    boolean outputLayer(String layerName);
}