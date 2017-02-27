package com.bluegosling.arquebus.core.view;

import pythagoras.f.Point;


public class Camera {
  private static final int VIEWPORT_HEIGHT = 30;
  
  private final float width;
  private final float height;
  private final float viewportWidth;
  private float curX;
  
  public Camera(float width, float height) {
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
  
  public Point viewToWorld(Point viewCoords) {
    return viewToWorld(viewCoords.x, viewCoords.y);
  }
  
  public Point viewToWorld(float viewX, float viewY) {
    return new Point(viewX * viewportWidth / width + getMinX(),
        (height - viewY) * VIEWPORT_HEIGHT / height);
  }
  
  public float viewScaleFactor() {
    return width / viewportWidth;
  }
}
