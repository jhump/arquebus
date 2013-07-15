package com.googlecode.arquebus.core;

import playn.core.PlayN;


public class FrameRateTracker {
  private final int durationTicks;
  private int count;
  private int startTick;
  private float rate = -1;
  
  public FrameRateTracker(int durationTicks) {
    this.durationTicks = durationTicks;
    startTick = PlayN.tick();
  }
  
  public static FrameRateTracker avgOverSec() {
    return new FrameRateTracker(1000);
  }

  public static FrameRateTracker avgOverMinute() {
    return new FrameRateTracker(60 * 1000);
  }
  
  public void mark() {
    int current = PlayN.tick();
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
      return count * 1000.0f / (PlayN.tick() - startTick);
    }
    return rate;
  }
}
