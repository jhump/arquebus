package com.googlecode.arquebus.core.model;

import com.googlecode.arquebus.core.Level;
import com.googlecode.arquebus.core.jbox2d.ContactListeners;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


public class GameModel {
  private final World world;
  private final VehicleModel vehicle;
  private final GroundModel ground;  
  
  public GameModel(Level level) {
    this(new World(new Vec2(0, -level.getGravity())), level);
  }
  
  public GameModel(World world, Level level) {
    this.world = world;
    ContactListeners.init(world);
    vehicle = new VehicleModel(world);
    ground = new GroundModel(world, level);
    ground.setVisibleExtents(0, 0);
  }
  
  World getWorld() {
    return world;
  }
  
  public VehicleModel getVehicle() {
    return vehicle;
  }
  
  public GroundModel getGround() {
    return ground;
  }
  
  public void update(int delta) {
    vehicle.update();
    float timeStep = delta / 1000.0f;
    world.step(timeStep, 6, 2);
  }
  
}
