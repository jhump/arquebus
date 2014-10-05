package com.googlecode.arquebus.core.model;

import playn.core.PlayN;

public class ArtilleryFactory {
  private final VehicleModel vehicle;
  
  private int lastMainFire;
  private int lastAlternateFire;
  
  private final int ticksBetweenMainFire = 100;
  private final int ticksBetweenAlternateFire = 3000;
  private final float mainDamage = 10;
  private final float alternateDamage = 1000;
  
  // TODO: make generic for use with enemy artillery and data-driven from level info
  ArtilleryFactory(VehicleModel vehicle) {
    this.vehicle = vehicle;
  }
  
  public ArtilleryModel tryFireMain() {
    if (lastMainFire == 0 || lastMainFire + ticksBetweenMainFire <= PlayN.tick()) {
      //return new Bullet(vehicle, start, target, mainDamage);
    }
    return null;
  }

  public ArtilleryModel tryFireAlternate() {
    if (lastMainFire == 0 || lastMainFire + ticksBetweenMainFire <= PlayN.tick()) {
      //return new Missile(...);
    }
    return null;
  }
}
