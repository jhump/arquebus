package com.googlecode.arquebus.core.view;


public interface Renderable<R> {
  int getZindex();
  void render(R r, Camera camera);
}
