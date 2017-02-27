package com.bluegosling.arquebus.core;

import playn.core.Platform;

public class FrameRateTracker {
   private final Platform plat;
   private final int durationTicks;
   private int count;
   private int startTick;
   private float rate = -1;
  
   public FrameRateTracker(Platform plat, int durationTicks) {
      this.plat = plat;
      this.durationTicks = durationTicks;
      startTick = plat.tick();
   }
  
   public static FrameRateTracker avgOverSec(Platform plat) {
      return new FrameRateTracker(plat, 1000);
   }

  public static FrameRateTracker avgOverMinute(Platform plat) {
    return new FrameRateTracker(plat, 60 * 1000);
  }
  
  public void mark() {
    int current = plat.tick();
    if (startTick + durationTicks <= current) {
      rate = count * 1000.0f / durationTicks;
      count = 0;
      // int arithmetic so next startTick is clamped to a multiple
      // of durationTicks
      int n = (current - startTick) / durationTicks;
      startTick += n * durationTicks;
    }
    count++;
  }
  
  public float fpsRate() {
    if (rate < 0) {
      return count * 1000.0f / (plat.tick() - startTick);
    }
    return rate;
  }
}
