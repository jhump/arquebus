package com.googlecode.arquebus.core;

import static playn.core.PlayN.graphics;

import com.googlecode.arquebus.core.controller.VehicleController;
import com.googlecode.arquebus.core.model.GameModel;
import com.googlecode.arquebus.core.view.GameView;
import com.googlecode.arquebus.core.view.Layers;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Font;
import playn.core.Font.Style;
import playn.core.Game;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;

public class ArquebusMain extends Game.Default {
  private final int level = 1;

  private FrameRateTracker fps;
  private FrameRateTracker fpsMinute;
  private GameModel model;
  private GameView renderer;
  private Canvas statsCanvas;
      
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
    fps = FrameRateTracker.avgOverSec();
    fpsMinute = FrameRateTracker.avgOverMinute();
  }
  
  private void loading() {
    int width = graphics().width();
    int height = graphics().height();
    Canvas canvas = Layers.addCanvas(graphics());
    canvas.setFillColor(Color.rgb(120, 20,  0)).fillRect(0,  0, width, height);
    canvas.setFillColor(Color.rgb(255, 255, 255)).setStrokeColor(Color.rgb(0, 0, 0)).setStrokeWidth(10);
    Font font = graphics().createFont("Arial", Style.BOLD, 60);
    TextLayout layout = graphics().layoutText("Loading...",
        new TextFormat().withFont(font));
    float x = (width - layout.width()) / 2;
    float y = (height - layout.height()) / 2;
    canvas.strokeText(layout, x, y).fillText(layout, x, y);
  }

  @Override
  public void update(int delta) {
    if (model == null || renderer == null) {
      return;
    }
    model.update(delta);
  }
  
  private void drawStats() {
    if (statsCanvas == null) {
      statsCanvas = Layers.addCanvas(graphics());
      statsCanvas.setFillColor(Color.rgb(255, 255, 255)).setStrokeColor(Color.rgb(0, 0, 0)).setStrokeWidth(1);
    }
    statsCanvas.clear();
    Font font = graphics().createFont("Arial", Style.PLAIN, 12);
    TextLayout layout = graphics().layoutText(
        String.format("Frame Rate: %4.2f (%4.2f over last minute)", fps.fpsRate(), fpsMinute.fpsRate()),
        new TextFormat().withFont(font));
    statsCanvas.strokeText(layout, 0, 0).fillText(layout, 0, 0);
  }
  

  @Override
  public void paint(float alpha) {
    if (model == null || renderer == null) {
      return;
    }
    renderer.paint();
    fps.mark(); fpsMinute.mark();
    drawStats();
  }
}
