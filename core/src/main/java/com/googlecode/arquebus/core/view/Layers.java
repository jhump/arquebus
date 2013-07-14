package com.googlecode.arquebus.core.view;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Graphics;
import playn.core.GroupLayer;
import playn.core.Surface;
import playn.core.SurfaceImage;


public final class Layers {
  private Layers() {
  }

  public static Canvas addCanvas(Graphics g) {
    return addCanvas(g, g.rootLayer());
  }

  public static Canvas addCanvas(Graphics g, GroupLayer l) {
    CanvasImage img = g.createImage(g.width(), g.height());
    (l == null ? g.rootLayer() : l).add(g.createImageLayer(img));
    return img.canvas();
  }
  
  public static Surface addSurface(Graphics g) {
    return addSurface(g, g.rootLayer());
  }

  public static Surface addSurface(Graphics g, GroupLayer l) {
    SurfaceImage img = g.createSurface(g.width(), g.height());
    (l == null ? g.rootLayer() : l).add(g.createImageLayer(img));
    return img.surface();
  }
  
  public static GroupLayer addContainer(Graphics g) {
    return addContainer(g, g.rootLayer());
  }

  public static GroupLayer addContainer(Graphics g, GroupLayer l) {
    GroupLayer layer = g.createGroupLayer();
    (l == null ? g.rootLayer() : l).add(layer);
    return layer;
  }
}
