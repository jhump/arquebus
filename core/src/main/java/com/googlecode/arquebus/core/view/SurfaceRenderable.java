package com.googlecode.arquebus.core.view;

import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Surface;


public abstract class SurfaceRenderable implements Renderable {
  private final GroupLayer parent;
  private Surface surface;
  
  SurfaceRenderable(GroupLayer parent) {
    this.parent = parent;
  }
  
  SurfaceRenderable() {
    this(null);
  }
  
  @Override
  public final void render(Graphics g, Camera camera) {
    if (surface == null) {
      surface = Layers.addSurface(g, parent);
    }
    render(surface, camera);
  }
  
  protected abstract void render(Surface layer, Camera camera);

}
