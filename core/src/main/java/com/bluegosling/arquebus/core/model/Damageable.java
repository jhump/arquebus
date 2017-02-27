package com.bluegosling.arquebus.core.model;


public interface Damageable {
  void applyDamage(float amount);
  float damageCapacity();
}
