package com.googlecode.arquebus.java;

import com.googlecode.arquebus.core.ArquebusMain;

import playn.core.PlayN;
import playn.java.JavaPlatform;

public class ArquebusMainJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    config.width = 1024;
    config.height = 640;
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config).setTitle("Arquebus");
    PlayN.run(new ArquebusMain());
  }
}
