package com.bluegosling.arquebus.core;

import com.bluegosling.arquebus.core.controller.VehicleController;
import com.bluegosling.arquebus.core.model.GameModel;
import com.bluegosling.arquebus.core.view.GameView;
import com.google.common.base.Throwables;
import playn.core.Canvas;
import playn.core.Clock;
import playn.core.Color;
import playn.core.Font;
import playn.core.Platform;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.Font.Style;
import playn.scene.CanvasLayer;
import playn.scene.SceneGame;
import pythagoras.f.IDimension;
import react.SignalView.Listener;
import react.Try;

public class Arquebus extends SceneGame {
   private enum State {
      INITIALIZING, RUNNING
   }
   
   private final int level = 1;
   private volatile State state;
   private FrameRateTracker fps;
   private FrameRateTracker fpsMinute;
   private GameModel model;
   private GameView view;
   private Canvas statsCanvas;
   private CanvasLayer statsLayer;
      
   public Arquebus(Platform plat) {
      super(plat, 33); // call update every 33ms (~30 times per second)
   }

   @Override
   public void update(Clock clock) {
      if (state == State.RUNNING) {
         if (model == null || view == null) {
            return;
         }
         model.update(clock);
         model.getVehicle().setAimingAt(view.getPointerWorldPosition());
      }
      super.update(clock);
   }

   @Override
   public void paint(Clock clock) {
      if (state == null) {
         init();
         state = State.INITIALIZING;
      } else if (state == State.RUNNING) {
         if (model == null || view == null) {
            return;
         }
         view.paint();
         fps.mark(); fpsMinute.mark();
         drawStats();
      }
      super.paint(clock);
   }

   private void init() {
      loading();
      Level.Loader.loadLevel(plat, level, new Listener<Try<Level>>() {
         @Override
         public void onEmit(Try<Level> event) {
            if (event.isSuccess()) {
               Level lvl = event.get();
               model = new GameModel(lvl);
               view = new GameView(model, lvl, plat.graphics());
               view.addToSceneGraph(rootLayer);
               new VehicleController(plat, model.getVehicle(), view);
            } else {
               Throwables.throwIfUnchecked(event.getFailure());
               throw new RuntimeException(event.getFailure());
            }
            state = State.RUNNING;
         }
      });
      fps = FrameRateTracker.avgOverSec(plat);
      fpsMinute = FrameRateTracker.avgOverMinute(plat);
   }
  
   private void loading() {
      IDimension dim = plat.graphics().viewSize;
      Canvas canvas = plat.graphics().createCanvas(dim);
      canvas.setFillColor(Color.rgb(120, 20,  0))
            .fillRect(0,  0, dim.width(), dim.height());
      canvas.setFillColor(Color.rgb(255, 255, 255))
            .setStrokeColor(Color.rgb(0, 0, 0))
            .setStrokeWidth(10);
      Font font = new Font("Arial", Style.BOLD, 60);
      TextLayout layout = plat.graphics().layoutText("Loading...", new TextFormat().withFont(font));
      float x = (dim.width() - layout.size.width()) / 2;
      float y = (dim.height() - layout.size.height()) / 2;
      canvas.strokeText(layout, x, y).fillText(layout, x, y);
      rootLayer.add(new CanvasLayer(plat.graphics(), canvas));
   }
  
   private void drawStats() {
      if (statsCanvas == null) {
         statsCanvas = plat.graphics().createCanvas(plat.graphics().viewSize);
         statsCanvas.setFillColor(Color.rgb(255, 255, 255))
               .setStrokeColor(Color.rgb(0, 0, 0)).setStrokeWidth(1);
         statsLayer = new CanvasLayer(plat.graphics(), statsCanvas);
         rootLayer.add(statsLayer);
      }
      statsLayer.begin();
      statsCanvas.clear();
      Font font = new Font("Arial", Style.PLAIN, 12);
      String msg = String.format("Frame Rate: %4.2f (%4.2f over last minute)",
            fps.fpsRate(), fpsMinute.fpsRate());
      TextLayout layout = plat.graphics().layoutText(msg, new TextFormat().withFont(font));
      statsCanvas.strokeText(layout, 0, 0).fillText(layout, 0, 0);
      statsLayer.end();
  }
}
