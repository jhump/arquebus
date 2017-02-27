package com.bluegosling.arquebus.java;

import playn.java.LWJGLPlatform;

import com.bluegosling.arquebus.core.Arquebus;

public class ArquebusJava {

  public static void main(String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    config.appName = "Arquebus";
    config.width = 800;
    config.height = 600;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new Arquebus(plat);
    plat.start();
  }
}
