package com.googlecode.arquebus.core.view;

import com.google.common.collect.Lists;
import com.googlecode.arquebus.core.Level;
import com.googlecode.arquebus.core.model.GameModel;
import com.googlecode.arquebus.core.model.VehicleModel;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Graphics;

import java.util.List;


public class GameView {
  private final Graphics g;
  private final Camera camera;
  private final VehicleModel vehicle;
  private final Background background;
  private final Canvas canvas;
  private final List<Renderable> staticElements = Lists.newArrayListWithCapacity(3);
  
  // TODO: enemies, artillery, etc
  
  public GameView(GameModel model, Level level, Graphics g) {
    this.g = g;
    this.camera = new Camera(g.width(), g.height());
    this.vehicle = model.getVehicle();
    this.background = new Background(g, level);
    CanvasImage image = g.createImage(g.width(), g.height());
    g.rootLayer().add(g.createImageLayer(image));
    this.canvas = image.canvas();
    staticElements.add(new GroundView(model.getGround()));
    staticElements.add(new VehicleView(model.getVehicle()));
  }
  
  public void paint() {
    camera.setX(vehicle.getCarBody().getWorldCenter().x);
    background.render(camera);
    for (Renderable r : staticElements) {
      r.render(g, camera);
    }    
  }
}
