package com.googlecode.arquebus.core.controller;

import com.googlecode.arquebus.core.model.VehicleModel;

import playn.core.Keyboard.Event;
import playn.core.Keyboard.Listener;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;


public class VehicleController {
  public VehicleController(final VehicleModel vehicle) {
    // TODO: no hardware keyboard? touch buttons instead?
    // TODO: analog controls of some sort?
    PlayN.keyboard().setListener(new Listener() {
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
    
    // TODO: use touch if no mouse
    PlayN.mouse();
  }
}
