package com.googlecode.arquebus.core.model;

import playn.core.PlayN;

import com.googlecode.arquebus.core.model.ArtilleryModel.Bullet;
import com.googlecode.arquebus.core.model.ArtilleryModel.Missile;
import com.googlecode.arquebus.core.model.ArtilleryModel.MissileExplosion;

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
     int now = PlayN.tick();
     if (lastMainFire == 0 || lastMainFire + ticksBetweenMainFire <= now) {
        lastMainFire = now;
        return new Bullet(vehicle, vehicle.getCarBody().getWorldCenter(),
              vehicle.getAimingAtVec(), mainDamage);
     }
     return null;
  }

  public ArtilleryModel tryFireAlternate() {
     int now = PlayN.tick();
     if (lastAlternateFire == 0 || lastAlternateFire + ticksBetweenAlternateFire <= now) {
        lastAlternateFire = now;
        MissileExplosion asplode = MissileExplosion.newBuilder()
              .blastDamage(alternateDamage).collisionDamage(mainDamage)
              .startingRadius(2).endingRadius(15).expansionTime(500).lingerTime(500)
              .build();
        // TODO: create body for missile with velocity and facing vehicle aim direction 
        //return new Missile(vehicle, body, asplode);
     }
     return null;
  }
}
