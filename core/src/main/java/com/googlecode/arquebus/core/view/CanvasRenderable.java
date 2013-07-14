package com.googlecode.arquebus.core.view;

import playn.core.Canvas;
import playn.core.Graphics;
import playn.core.GroupLayer;


public abstract class CanvasRenderable implements Renderable {
  private final GroupLayer parent;
  private Canvas canvas;
  
  CanvasRenderable(GroupLayer parent) {
    this.parent = parent;
  }
  
  CanvasRenderable() {
    this(null);
  }
  
  @Override
  public final void render(Graphics g, Camera camera) {
    if (canvas == null) {
      canvas = Layers.addCanvas(g, parent);
    }
    render(canvas, camera);
  }
  
  protected abstract void render(Canvas layer, Camera camera);

}
