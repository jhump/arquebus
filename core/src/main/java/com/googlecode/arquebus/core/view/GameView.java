package com.googlecode.arquebus.core.view;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.arquebus.core.Level;
import com.googlecode.arquebus.core.model.GameModel;
import com.googlecode.arquebus.core.model.VehicleModel;

import playn.core.Canvas;
import playn.core.Graphics;
import playn.core.Surface;
import pythagoras.f.Point;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class GameView {
  private static class Item<R> {
    private final Class<R> type;
    private final Renderable<R> r;
    
    private Item(Class<R> type, Renderable<R> r) {
      this.type = type;
      this.r = r;
    }
    
    public static Item<Canvas> canvasItem(Renderable<Canvas> c) {
      return new Item<Canvas>(Canvas.class, c);
    }

    public static Item<Surface> surfaceItem(Renderable<Surface> s) {
      return new Item<Surface>(Surface.class, s);
    }

    public Class<R> getType() {
      return type;
    }
    
    public Renderable<R> getItem() {
      return r;
    }
  }
  
  private static class Layer<R> {
    private final Class<R> type;
    private final R r;
    
    private Layer(Class<R> type, R r) {
      this.type = type;
      this.r = r;
    }
    
    public static Layer<Canvas> canvasLayer(Canvas c) {
      return new Layer<Canvas>(Canvas.class, c);
    }

    public static Layer<Surface> surfaceLayer(Surface s) {
      return new Layer<Surface>(Surface.class, s);
    }

    public Class<R> getType() {
      return type;
    }
    
    public R getLayer() {
      return r;
    }
  }
  
  private final Graphics g;
  private final Camera camera;
  private final VehicleModel vehicle;
  private final Background background;
  private final Point pointerViewPosition;
  
  private boolean isDirty = true;
  private final List<Layer<?>> layers = Lists.newLinkedList();
  private final TreeMap<Integer, Item<?>> renderables = Maps.newTreeMap();
  
  // TODO: enemies, etc
  
  public GameView(GameModel model, Level level, Graphics g) {
    this.g = g;
    this.camera = new Camera(g.width(), g.height());
    this.vehicle = model.getVehicle();
    this.background = new Background(level);
    pointerViewPosition = new Point(g.width(), 0); // init pointer to upper-right corner
    
    addCanvasRenderable(new GroundView(model.getGround()));
    addCanvasRenderable(new VehicleView(model.getVehicle()));
    addCanvasRenderable(new ArtilleryView(model));
  }
  
  void addCanvasRenderable(Renderable<Canvas> c) {
    addItem(Item.canvasItem(c));
  }
  
  void addSurfaceRenderable(Renderable<Surface> s) {
    addItem(Item.surfaceItem(s));
  }
  
  public void setPointerViewPosition(float x, float y) {
    pointerViewPosition.set(x, y);
  }

  public Point getPointerWorldPosition() {
    return camera.viewToWorld(pointerViewPosition);
  }

  private <R> void addItem(Item<R> item) {
    int zIndex = item.getItem().getZindex();
    renderables.put(zIndex, item);
    if (!isDirty) {
      // may need to set dirty flag if new item requires different interleaving of layer types
      Map.Entry<Integer, Item<?>> previous = renderables.lowerEntry(zIndex);
      Map.Entry<Integer, Item<?>> next = renderables.higherEntry(zIndex);
      if (previous == null) {
        if (next.getValue().getType() != item.getType()) {
          isDirty = true;
        }
      } else if (next == null) {
        if (previous.getValue().getType() != item.getType()) {
          isDirty = true;
        }
      } else {
        if (next.getValue().getType() != item.getType()
            && previous.getValue().getType() != item.getType()) {
          isDirty = true;
        }
      }
    }
  }
  
  private void createLayers() {
    g.rootLayer().clear();
    background.addBackground(g.rootLayer());
    layers.clear();
    Class<?> lastType = null;
    for (Item<?> item : renderables.values()) {
      Class<?> type = item.getType();
      if (item.getType() != lastType) {
        if (item.getType() == Canvas.class) {
          layers.add(Layer.canvasLayer(Layers.addCanvas(g).save()));
        } else if (item.getType() == Surface.class) {
          layers.add(Layer.surfaceLayer(Layers.addSurface(g).save()));
        } else {
           throw new IllegalStateException("Unrecognized layer type: " + item.getType());
        }
      }
      lastType = type;
    }
    isDirty = false;
  }
  
  public void paint() {
    if (isDirty) {
      createLayers();
    }
    
    camera.setX(vehicle.getCarBody().getWorldCenter().x);
    background.update(camera);
    
    Class<?> lastType = null;
    Iterator<Layer<?>> iter = layers.iterator();
    Layer<?> current = null;
    
    //TODO: fix APIs so we don't need all of these conditionals and casts...
    
    for (Item<?> item : renderables.values()) {
      Class<?> type = item.getType();
      if (item.getType() != lastType || current == null) {
        current = iter.next();
        if (type != current.getType()) {
          // ruh roh!
           throw new IllegalStateException(
                 "Incorrect layer type. Got " + current.getType() + ", expecting " + type);
        }
        if (type == Canvas.class) {
          Canvas canvas = (Canvas) current.getLayer();
          canvas.restore().save().clear();
        } else if (type == Surface.class) {
          Surface surface = (Surface) current.getLayer();
          surface.restore().save().clear();
        } else {
           throw new IllegalStateException("Unrecognized layer type: " + item.getType());
        }
      }
      if (type == Canvas.class) {
        Canvas canvas = (Canvas) current.getLayer();
        @SuppressWarnings("unchecked")
        Renderable<Canvas> r = (Renderable<Canvas>) item.getItem();
        r.render(canvas.restore().save(), camera);
      } else if (type == Surface.class) {
        Surface surface = (Surface) current.getLayer();
        @SuppressWarnings("unchecked")
        Renderable<Surface> r = (Renderable<Surface>) item.getItem();
        r.render(surface.restore().save(), camera);
      }
      lastType = type;
    }    
  }
}
