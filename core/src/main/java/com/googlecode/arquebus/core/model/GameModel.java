package com.googlecode.arquebus.core.model;

import com.google.common.collect.Sets;
import com.googlecode.arquebus.core.Level;
import com.googlecode.arquebus.core.jbox2d.ContactListeners;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;


public class GameModel {
  private final World world;
  private final VehicleModel vehicle;
  private final GroundModel ground;
  private final HashSet<ArtilleryModel> artillery = Sets.newHashSet();
  
  public GameModel(Level level) {
    this(new World(new Vec2(0, -level.getGravity())), level);
  }
  
  public GameModel(World world, Level level) {
    this.world = world;
    ContactListeners.init(world);
    ground = new GroundModel(world, level);
    ground.setVisibleExtents(0, 0);
    vehicle = new VehicleModel(this);
  }
  
  public World getWorld() {
    return world;
  }
  
  public VehicleModel getVehicle() {
    return vehicle;
  }
  
  public GroundModel getGround() {
    return ground;
  }
  
  public Iterable<ArtilleryModel> getArtillery() {
    return Collections.unmodifiableCollection(artillery);
  }
  
  public void addArtillery(ArtilleryModel a) {
    artillery.add(a);
  }
  
  public void update(int delta) {
    vehicle.update();
    float timeStep = delta / 1000.0f;
    world.step(timeStep, 6, 2);
    for (Iterator<ArtilleryModel> iter = artillery.iterator(); iter.hasNext();) {
      ArtilleryModel a = iter.next();
      if (!a.update(delta)) {
        iter.remove();
      }
    }
  }
  
}
