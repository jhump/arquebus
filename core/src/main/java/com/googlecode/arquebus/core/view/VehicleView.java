package com.googlecode.arquebus.core.view;

import com.googlecode.arquebus.core.model.VehicleModel;

import org.jbox2d.common.Vec2;

import playn.core.Canvas;
import playn.core.Color;
import pythagoras.f.Point;


public class VehicleView implements Renderable<Canvas> {
  private final VehicleModel vehicle;
  
  VehicleView(VehicleModel vehicle) {
    this.vehicle = vehicle;
  }
  
  @Override
  public int getZindex() {
    return 0;
  }
  
  @Override
  public void render(Canvas canvas, Camera camera) {
    float scale = camera.viewScaleFactor();
    
    Vec2 pos = vehicle.getCarBody().getWorldCenter();
    Point translate = camera.worldToView(pos.x, pos.y);
    
    canvas.translate(translate.x, translate.y)
        .scale(scale, scale)
        .rotate(-vehicle.getCarBody().getAngle());
    pos = vehicle.getCarBody().getLocalCenter();
    canvas.setFillColor(Color.rgb(100, 100, 100)).fillRect(-2.33f - pos.x, -0.2f - pos.y, 4.7f, 1.57f);
    
    pos = vehicle.getRearWheelBody().getWorldCenter();
    translate = camera.worldToView(pos.x, pos.y);
    canvas.restore().save()
        .translate(translate.x, translate.y)
        .scale(scale, scale);
    canvas.setFillColor(Color.rgb(0, 0, 0)).fillCircle(0, 0, 0.6f);

    pos = vehicle.getFrontWheelBody().getWorldCenter();
    translate = camera.worldToView(pos.x, pos.y);
    canvas.restore().save()
        .translate(translate.x, translate.y)
        .scale(scale, scale);
    canvas.setFillColor(Color.rgb(0, 0, 0)).fillCircle(0, 0, 0.6f);

    // show where we are trying to aim
    translate = camera.worldToView(vehicle.getAimingAt());
    canvas.restore().save();
    canvas.setFillColor(Color.rgb(255, 200, 0)).fillCircle(translate.x, translate.y, 5);
  }
}
