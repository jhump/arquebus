package com.googlecode.arquebus.core.model;


public interface Damageable {
  void applyDamage(float amount);
  float damageCapacity();
}
