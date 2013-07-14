package com.googlecode.arquebus.core.view;

import com.google.common.collect.Multimap;
import com.googlecode.arquebus.core.Level;

import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Layer;


public class Background {
  private final Multimap<Float, Layer> layers;
  
  public Background(Graphics g, Level level) {
    this.layers = level.getBackgroundLayers();
    init(g);
  }
  
  private void init(Graphics g) {
    GroupLayer root = g.rootLayer();
    root.clear();
    for (Layer layer : layers.values()) {
      // TODO: layers are sorted in order of increasing animation velocity, but should be added
      // in reverse order (so slowest is at the back)
      root.add(layer);
    }
  }

  public void render(Camera camera) {
    // TODO update animated layer positions based on camera position
  }
}
