package com.googlecode.arquebus.core;

import com.googlecode.arquebus.core.model.GameModel;
import com.googlecode.arquebus.core.model.GroundModel;
import com.googlecode.arquebus.core.model.GroundModel.Segment;
import com.googlecode.arquebus.core.model.VehicleModel;

import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;

import playn.java.JavaPlatform;


public class ArquebusJBox2DTest extends TestbedTest {

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
    JavaPlatform.register();
    GameModel model = new GameModel(m_world, new Level.Builder().build());
    ground = model.getGround();
    vehicle = model.getVehicle();
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
  public float getDefaultCameraScale() {
    return 15;
  }
  
  @Override
  public synchronized void step(TestbedSettings settings) {
    
    vehicle.update();
    
    super.step(settings);
    
    addTextLine("Keys: right (forward) = w, left (reverse) = s");
    addTextLine(vehicle.getThrottleDisposition().name().toLowerCase());
    addTextLine("wheel 1: " + String.format("%3.4f", vehicle.getRearWheelBody().getAngularVelocity()) + " (" + (vehicle.rearWheelTouching() ? "y" : "n") + ")"
        + ", wheel 2: " + String.format("%3.4f", vehicle.getFrontWheelBody().getAngularVelocity()) + " (" + (vehicle.frontWheelTouching() ? "y" : "n") + ")"
        + ", body: " + String.format("%3.4f", vehicle.getCarBody().getLinearVelocity().length()) + " (" + (vehicle.carTouching() ? "y" : "n") + ")");
    Float lo = null;
    Segment last = null;
    for (Segment segment : ground.segments()) {
      if (lo == null) {
        lo = segment.getLine().x1;
      }
      last = segment;
    }
    double hi = last.getLine().x2;
    
    addTextLine("ground range: " + lo + " -> " + hi + "(" + ground.getBody().m_fixtureCount + " fixtures)");
    
    getCamera().setCamera(vehicle.getCarBody().getPosition());
    float centerX = vehicle.getCarBody().getPosition().x;
    ground.setVisibleExtents(centerX, centerX);
  }
}
