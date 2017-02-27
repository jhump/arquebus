package com.bluegosling.arquebus.core.controller;

import com.bluegosling.arquebus.core.model.VehicleModel;
import com.bluegosling.arquebus.core.view.GameView;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.Event;
import playn.core.Mouse.MotionEvent;
import playn.core.Platform;
import react.SignalView.Listener;


public class VehicleController {
   
   public VehicleController(Platform plat, final VehicleModel vehicle, final GameView gameView) {
      // TODO: no hardware keyboard? touch buttons instead
      // TODO: analog controls of some sort?
      plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
         private boolean forward = false;
         private boolean back = false;

         @Override
         public void onEmit(KeyEvent event) {
            if (event.down) {
               switch (event.key) {
                  case W: case D: case RIGHT:
                     forward = true;
                     vehicle.setThrottle(1);
                     break;
                  case S: case A: case LEFT:
                     back = true;
                     vehicle.setThrottle(-1);
                     break;
                  case R:
                     vehicle.reset();
                  default: // nothing
               }
            } else {
               switch (event.key) {
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
         }
      });

    
      // TODO: use mouse for turret to track cursor location and for clicking (shooting)
      plat.input().mouseEvents.connect(new Listener<Mouse.Event>() {
         @Override
         public void onEmit(Event event) {
            if (event instanceof ButtonEvent) {
               ButtonEvent e = (ButtonEvent) event;
               switch (e.button) {
                  case LEFT:
                     vehicle.setMainFiring(e.down);
                     break;
                  case RIGHT:
                     vehicle.setAlternateFiring(e.down);
                     break;
                  default:
               }
            } else if (event instanceof MotionEvent) {
               gameView.setPointerViewPosition(event.x(), event.y());
            }
         }
      });
    
      // TODO: use touch for shooting (when no mouse)
      // TODO: use touch buttons for forward/backward (when no keyboard)
      //plat.input().touchEvents.connect(...);
      // TODO: use pointer for updating turret location when shooting (when no mouse)
      //new Pointer(plat).events.connect(...);
   }
}
