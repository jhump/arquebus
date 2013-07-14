package com.googlecode.arquebus.core;

import static playn.core.PlayN.graphics;

import com.googlecode.arquebus.core.controller.VehicleController;
import com.googlecode.arquebus.core.model.GameModel;
import com.googlecode.arquebus.core.view.GameView;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Font.Style;
import playn.core.Game;
import playn.core.TextFormat;
import playn.core.TextFormat.Alignment;
import playn.core.TextLayout;
import playn.core.util.Callback;

public class ArquebusMain extends Game.Default {

  private final int level = 1;
  
  private GameModel model;
  private GameView renderer;
      
  public ArquebusMain() {
    super(33); // call update every 33ms (~30 times per second)
  }

  @Override
  public void init() {
    loading();
    Level.Loader.loadLevel(level, new Callback<Level>() {
      @Override
      public void onSuccess(Level result) {
        model = new GameModel(result);
        renderer = new GameView(model, result, graphics());
        new VehicleController(model.getVehicle());
      }

      @Override
      public void onFailure(Throwable cause) {
        // TODO Auto-generated method stub
      }
    });
  }
  
  private void loading() {
    int width = graphics().width();
    int height = graphics().height();
    CanvasImage img = graphics().createImage(width, height);
    Canvas canvas = img.canvas();
    canvas.setFillColor(Color.rgb(120, 20,  0)).fillRect(0,  0, width, height);
    canvas.setFillColor(Color.rgb(255, 255, 255)).setStrokeColor(Color.rgb(0, 0, 0)).setStrokeWidth(10);
    Font font = graphics().createFont("Arial", Style.BOLD, 60);
    TextLayout layout = graphics().layoutText("Loading...",
        new TextFormat().withFont(font).withAlignment(Alignment.CENTER));
    float x = (width - layout.width()) / 2;
    float y = (height - layout.height()) / 2;
    canvas.strokeText(layout, x, y).fillText(layout, x, y);
    graphics().rootLayer().add(graphics().createImageLayer(img));
  }

  @Override
  public void update(int delta) {
    if (model == null || renderer == null) {
      return;
    }
    model.update(delta);
  }

  @Override
  public void paint(float alpha) {
    if (model == null || renderer == null) {
      return;
    }
    renderer.paint();
  }
}
