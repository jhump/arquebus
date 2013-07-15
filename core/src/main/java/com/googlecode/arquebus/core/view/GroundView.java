package com.googlecode.arquebus.core.view;

import com.googlecode.arquebus.core.model.GroundModel;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Path;
import pythagoras.f.Point;


public class GroundView implements Renderable<Canvas> {
  private final GroundModel ground;
  
  GroundView(GroundModel ground) {
    this.ground = ground;
  }

  @Override
  public int getZindex() {
    return -1000;
  }
  
  @Override
  public void render(Canvas canvas, Camera camera) {
    Path path = canvas.createPath();
    Point p0 = camera.worldToView(camera.getMaxX(), camera.getMinY());
    Point p1 = camera.worldToView(camera.getMinX(), camera.getMinY());
    path.moveTo(p0.x, p0.y);
    path.lineTo(p1.x, p1.y);
    ground.setVisibleExtents(camera.getMinX(), camera.getMaxX());
    for (Point p : ground.visiblePoints()) {
      Point viewp = camera.worldToView(p);
      path.lineTo(viewp.x, viewp.y);
    }
    path.close();
    canvas.setFillColor(Color.rgb(110, 50, 20)).fillPath(path);
  }
}
