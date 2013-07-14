package com.googlecode.arquebus.core.view;

import playn.core.Graphics;
import playn.core.GroupLayer;

public abstract class ContainerRenderable implements Renderable {
  private final GroupLayer parent;
  private GroupLayer group;
  
  ContainerRenderable(GroupLayer parent) {
    this.parent = parent;
  }
  
  ContainerRenderable() {
    this(null);
  }
  
  @Override
  public final void render(Graphics g, Camera camera) {
    if (group == null) {
      group = Layers.addContainer(g, parent);
    }
    group.clear();
    createChildLayers(g, group);
    render(group, camera);
  }
  
  protected void createChildLayers(Graphics g, GroupLayer layer) {
    // optional step to create children during initialization
  }
  
  protected abstract void render(GroupLayer layer, Camera camera);
}
