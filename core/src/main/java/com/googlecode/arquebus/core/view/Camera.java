package com.googlecode.arquebus.core.view;

import pythagoras.f.Point;


public class Camera {
  private static final int VIEWPORT_HEIGHT = 16;
  
  private final int width;
  private final int height;
  private final float viewportWidth;
  private float curX;
  
  public Camera(int width, int height) {
    this.width = width;
    this.height = height;
    viewportWidth = width * VIEWPORT_HEIGHT / height;
  }
  
  public void setX(float x) {
    this.curX = x;
  }

  public float getMinX() {
    return curX - viewportWidth / 2;
  }
  
  public float getMaxX() {
    return curX + viewportWidth / 2;
  }
  
  public float getMinY() {
    return 0;
  }
  
  public float getMaxY() {
    return VIEWPORT_HEIGHT;
  }
  
  public boolean isXinView(float x) {
    return x >= getMinX() && x <= getMaxX();
  }
  
  public boolean isInView(Point worldCoords) {
    return isInView(worldCoords.x, worldCoords.y);
  }
  
  public boolean isInView(float worldX, float worldY) {
    return isXinView(worldX)
        && worldY >= getMinY() && worldY <= getMaxY();
  }
  
  public Point worldToView(Point worldCoords) {
    return worldToView(worldCoords.x, worldCoords.y);
  }
  
  public Point worldToView(float worldX, float worldY) {
    return new Point((worldX - getMinX()) * width / viewportWidth,
        height - (worldY * height / VIEWPORT_HEIGHT));
  }
  
  public float viewScaleFactor() {
    return width / viewportWidth;
  }
}
