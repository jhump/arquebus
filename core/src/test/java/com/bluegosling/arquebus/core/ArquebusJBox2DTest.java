package com.bluegosling.arquebus.core;

import com.bluegosling.arquebus.core.Level;
import com.bluegosling.arquebus.core.model.GameModel;
import com.bluegosling.arquebus.core.model.GroundModel;
import com.bluegosling.arquebus.core.model.VehicleModel;
import com.bluegosling.arquebus.core.model.GroundModel.Segment;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jbox2d.common.Vec2;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;
import playn.core.Clock;
import playn.scene.Layer;

public class ArquebusJBox2DTest extends TestbedTest {

   private long firstTick;
   private GroundModel ground;
   private VehicleModel vehicle;

   @Override
   public boolean isSaveLoadEnabled() {
      return false;
   }

   @Override
   public String getTestName() {
      return "Arquebus";
   }

   @Override
   public void initTest(boolean deserialized) {
      if (deserialized) {
         return;
      }

      GameModel model = new GameModel(m_world, new Level() {
         @Override
         public int levelNumber() {
            return 1;
         }

         @Override
         public Multimap<Float, Layer> getBackgroundLayers() {
            return ImmutableMultimap.of();
         }

         @Override
         public int getSeed() {
            return 1000;
         }

         @Override
         public float getGroundJaggedness() {
            return 0;
         }

         @Override
         public float getGroundHeightVariance() {
            return 0;
         }

         @Override
         public float getGroundFriction() {
            return 0.9f;
         }

         @Override
         public float getGravity() {
            return 9.8f;
         }
      });
      ground = model.getGround();
      vehicle = model.getVehicle();
      firstTick = System.currentTimeMillis();
   }

   @Override
   public void keyPressed(char argKeyChar, int argKeyCode) {
      switch (argKeyChar) {
         case 'w':
            vehicle.setThrottle(1.0f);
            break;

         case 's':
            vehicle.setThrottle(-1.0f);
            break;
      }
   }

   @Override
   public void keyReleased(char argKeyChar, int argKeyCode) {
      super.keyReleased(argKeyChar, argKeyCode);
      switch (argKeyChar) {
         case 'w':
         case 's':
            vehicle.setThrottle(0);
            break;
      }
   }

   @Override
   public synchronized void step(TestbedSettings settings) {
      Clock clock = new Clock();
      clock.tick = (int)(System.currentTimeMillis() - firstTick);
      vehicle.update(clock);

      super.step(settings);

      addTextLine("Keys: right (forward) = w, left (reverse) = s");
      addTextLine(vehicle.getThrottleDisposition().name().toLowerCase());
      addTextLine(
            "wheel 1: " + String.format("%3.4f", vehicle.getRearWheelBody().getAngularVelocity())
                  + " (" + (vehicle.rearWheelTouching() ? "y" : "n") + ")" + ", wheel 2: "
                  + String.format("%3.4f", vehicle.getFrontWheelBody().getAngularVelocity()) + " ("
                  + (vehicle.frontWheelTouching() ? "y" : "n") + ")" + ", body: "
                  + String.format("%3.4f", vehicle.getCarBody().getLinearVelocity().length()) + " ("
                  + (vehicle.carTouching() ? "y" : "n") + ")");
      Float lo = null;
      Segment last = null;
      for (Segment segment : ground.segments()) {
         if (lo == null) {
            lo = segment.getLine().x1;
         }
         last = segment;
      }
      double hi = last.getLine().x2;

      addTextLine("ground range: " + lo + " -> " + hi + "(" + ground.getBody().m_fixtureCount
            + " fixtures)");

      Vec2 pos = vehicle.getCarBody().getPosition();
      pos.x -= 30;
      pos.y = 40;
      setCamera(pos);
      float centerX = vehicle.getCarBody().getPosition().x;
      ground.setVisibleExtents(centerX, centerX);
   }
   
   @Override
   public float getDefaultCameraScale() {
      return 10;
   }
}
