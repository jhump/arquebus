package com.googlecode.arquebus.core.view;

import com.googlecode.arquebus.core.model.VehicleModel;

import org.jbox2d.common.Vec2;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Graphics;
import playn.core.GroupLayer;
import pythagoras.f.Point;


public class VehicleView extends ContainerRenderable {
  private final VehicleModel vehicle;
  private Canvas carCanvas;
  private Canvas rearWheelCanvas;
  private Canvas frontWheelCanvas;
  
  VehicleView(VehicleModel vehicle) {
    this.vehicle = vehicle;
  }
  
  @Override
  protected void createChildLayers(Graphics g, GroupLayer layer) {
    carCanvas = Layers.addCanvas(g, layer);
    carCanvas.save();
    rearWheelCanvas = Layers.addCanvas(g, layer);
    rearWheelCanvas.save();
    frontWheelCanvas = Layers.addCanvas(g, layer);
    frontWheelCanvas.save();
  }

  @Override
  public void render(GroupLayer layer, Camera camera) {
    float scale = camera.viewScaleFactor();
    
    Vec2 pos = vehicle.getCarBody().getWorldCenter();
    Point translate = camera.worldToView(pos.x, pos.y);
    carCanvas.restore().save()
        .translate(translate.x, translate.y)
        .scale(scale, scale)
        .rotate(-vehicle.getCarBody().getAngle());
    
    pos = vehicle.getRearWheelBody().getWorldCenter();
    translate = camera.worldToView(pos.x, pos.y);
    rearWheelCanvas.restore().save()
        .translate(translate.x, translate.y)
        .scale(scale, scale);

    pos = vehicle.getFrontWheelBody().getWorldCenter();
    translate = camera.worldToView(pos.x, pos.y);
    frontWheelCanvas.restore().save()
        .translate(translate.x, translate.y)
        .scale(scale, scale);

    pos = vehicle.getCarBody().getLocalCenter();
    carCanvas.clear().setFillColor(Color.rgb(100, 100, 100)).fillRect(-2.33f - pos.x, -0.2f - pos.y, 4.7f, 1.57f);
    rearWheelCanvas.clear().setFillColor(Color.rgb(0, 0, 0)).fillCircle(0, 0, 0.6f);
    frontWheelCanvas.clear().setFillColor(Color.rgb(0, 0, 0)).fillCircle(0, 0, 0.6f);
  }
}
