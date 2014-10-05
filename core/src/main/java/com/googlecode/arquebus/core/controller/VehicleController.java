package com.googlecode.arquebus.core.controller;

import com.googlecode.arquebus.core.model.VehicleModel;
import com.googlecode.arquebus.core.view.GameView;

import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.PlayN;


public class VehicleController {
   
  public VehicleController(final VehicleModel vehicle, final GameView gameView) {
    // TODO: no hardware keyboard? touch buttons instead
    // TODO: analog controls of some sort?
    PlayN.keyboard().setListener(new Keyboard.Listener() {
      private boolean forward = false;
      private boolean back = false;
      
      @Override
      public void onKeyDown(Event event) {
        switch (event.key()) {
          case W: case D: case RIGHT:
            forward = true;
            vehicle.setThrottle(1);
            break;
          case S: case A: case LEFT:
            back = true;
            vehicle.setThrottle(-1);
            break;
          default: // nothing
        }
      }
      
      @Override
      public void onKeyUp(Event event) {
        switch (event.key()) {
          case W: case D: case RIGHT:
            forward = false;
            vehicle.setThrottle(back ? -1 : 0);
            break;
          case S: case A: case LEFT:
            back = false;
            vehicle.setThrottle(forward ? 1 : 0);
            break;
          default: // nothing
        } 
      }
      
      @Override
      public void onKeyTyped(TypedEvent event) {
        if (event.typedChar() == 'r' || event.typedChar() == 'R') {
          vehicle.reset();
        }
      }
    });
    
    // TODO: use mouse for turret to track cursor location and for clicking (shooting)
    PlayN.mouse().setListener(new Mouse.Listener() {
      @Override
      public void onMouseWheelScroll(WheelEvent event) {
      }
      
      @Override
      public void onMouseDown(ButtonEvent event) {
        switch (event.button()) {
          case Mouse.BUTTON_LEFT:
            vehicle.setMainFiring(true);
            break;
          case Mouse.BUTTON_RIGHT:
            vehicle.setAlternateFiring(true);
            break;
          default:
        }
      }
      
      @Override
      public void onMouseUp(ButtonEvent event) {
        switch (event.button()) {
          case Mouse.BUTTON_LEFT:
            vehicle.setMainFiring(false);
            break;
          case Mouse.BUTTON_RIGHT:
            vehicle.setAlternateFiring(false);
            break;
          default:
        }
      }
      
      @Override
      public void onMouseMove(MotionEvent event) {
        gameView.setPointerViewPosition(event.x(), event.y());
      }
    });
    
    // TODO: use touch for shooting (when no mouse)
    // TODO: use touch buttons for forward/backward (when no keyboard)
    PlayN.touch();
    // TODO: use pointer for updating turret location when shooting (when no mouse)
    PlayN.pointer();
  }
}
