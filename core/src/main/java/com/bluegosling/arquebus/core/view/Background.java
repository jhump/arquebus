package com.bluegosling.arquebus.core.view;

import com.bluegosling.arquebus.core.Level;
import com.google.common.collect.Multimap;
import playn.scene.GroupLayer;
import playn.scene.Layer;


public class Background {
  private final Multimap<Float, Layer> layers;
  
  public Background(Level level) {
    this.layers = level.getBackgroundLayers();
  }
  
  public void addBackground(GroupLayer ls) {
    for (Layer layer : layers.values()) {
      ls.add(layer);
    }
  }

  public void update(Camera camera) {
    // TODO update animated layer positions based on camera position
  }
}
