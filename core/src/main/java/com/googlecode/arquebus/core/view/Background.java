package com.googlecode.arquebus.core.view;

import com.google.common.collect.Multimap;
import com.googlecode.arquebus.core.Level;

import playn.core.GroupLayer;
import playn.core.Layer;


public class Background {
  private final Multimap<Float, Layer> layers;
  
  public Background(Level level) {
    this.layers = level.getBackgroundLayers();
  }
  
  public void addBackground(GroupLayer g) {
    for (Layer layer : layers.values()) {
      g.add(layer);
    }
  }

  public void update(Camera camera) {
    // TODO update animated layer positions based on camera position
  }
}
