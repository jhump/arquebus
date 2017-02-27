package com.bluegosling.arquebus.core.view;

import com.bluegosling.arquebus.core.Level;
import com.bluegosling.arquebus.core.model.GameModel;
import com.bluegosling.arquebus.core.model.VehicleModel;
import playn.core.Canvas;
import playn.core.Graphics;
import playn.core.Surface;
import playn.scene.CanvasLayer;
import playn.scene.GroupLayer;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import pythagoras.f.Point;

import java.util.TreeSet;


public class GameView {
   private static class Item<R> implements Comparable<Item<?>> {
      private static int stampGen = 0;
      
      private final Renderable<R> r;
      private final Class<R> type;
      private final int stamp;
      private RenderableGroup<R> g;
      
      private Item(Renderable<R> r, Class<R> type) {
         this.r = r;
         this.type = type;
         this.stamp = stampGen++;
      }
      
      Renderable<R> getItem() {
         return r;
      }
      
      Class<R> getType() {
         return type;
      }
      
      RenderableGroup<R> getGroup() {
         return g;
      }
      
      void setGroup(RenderableGroup<R> g) {
         this.g = g;
      }
      
      static Item<Canvas> canvasItem(Renderable<Canvas> r) {
         return new Item<>(r, Canvas.class);
      }

      static Item<Surface> surfaceItem(Renderable<Surface> r) {
         return new Item<>(r, Surface.class);
      }

      @Override
      public int compareTo(Item<?> o) {
         int c = Integer.compare(getItem().getZindex(), o.getItem().getZindex());
         if (c != 0) {
            return c;
         }
         return Integer.compare(stamp, o.stamp);
      }
   }
   
   private interface RenderableGroup<R> {
      Item<R> getFirst();
      void setFirst(Item<R> item);
      Class<R> getType();
      Layer asLayer();
   }
   
   private class CanvasGroup extends CanvasLayer implements RenderableGroup<Canvas> {
      private final Canvas c;
      private Item<Canvas> firstItem;

      CanvasGroup(Graphics g, Canvas c) {
         super(g, c);
         this.c = c;
      }
      
      public Class<Canvas> getType() {
         return Canvas.class;
      }

      @Override
      public Item<Canvas> getFirst() {
         return firstItem;
      }

      @Override
      public void setFirst(Item<Canvas> item) {
         this.firstItem = item;
      }

      @Override
      public Layer asLayer() {
         return this;
      }
      
      @Override
      protected void paintImpl(Surface surf) {
         begin();
         c.restore().save().clear();
         for (Item<?> i : renderables.tailSet(firstItem)) {
            if (i.getGroup() == this) {
               @SuppressWarnings("unchecked")
               Item<Canvas> ci = (Item<Canvas>) i; 
               ci.getItem().render(c, camera);
            }
         }
         end();
         super.paintImpl(surf);
      }
   }
   
   private class SurfaceGroup extends Layer implements RenderableGroup<Surface> {
      private Item<Surface> firstItem;

      public Class<Surface> getType() {
         return Surface.class;
      }

      @Override
      public Item<Surface> getFirst() {
         return firstItem;
      }

      @Override
      public void setFirst(Item<Surface> item) {
         this.firstItem = item;
      }

      @Override
      public Layer asLayer() {
         return this;
      }

      @Override
      protected void paintImpl(Surface surf) {
         for (Item<?> i : renderables.tailSet(firstItem)) {
            if (i.getGroup() == this) {
               @SuppressWarnings("unchecked")
               Item<Surface> si = (Item<Surface>) i; 
               si.getItem().render(surf, camera);
            }
         }
      }
   }

   private final GroupLayer backgroundLayer;
   private final GroupLayer drawLayers;
   private final Graphics g;
   private final Camera camera;
   private final VehicleModel vehicle;
   private final Background background;
   private final Point pointerViewPosition;
   private final TreeSet<Item<?>> renderables = new TreeSet<>();
   private boolean isDirty;

   // TODO: enemies, etc

   public GameView(GameModel model, Level level, Graphics g) {
      IDimension dim = g.viewSize;
      this.backgroundLayer = new GroupLayer(dim.width(), dim.height());
      this.drawLayers = new GroupLayer(dim.width(), dim.height());
      this.g = g;
      this.camera = new Camera(dim.width(), dim.height());
      this.vehicle = model.getVehicle();
      this.background = new Background(level);
      background.addBackground(backgroundLayer);
      pointerViewPosition = new Point(dim.width(), 0); // init pointer to upper-right corner

      addCanvasRenderable(new GroundView(model.getGround()));
      addCanvasRenderable(new VehicleView(model.getVehicle()));
      addCanvasRenderable(new ArtilleryView(model));
   }
   
   public void addToSceneGraph(GroupLayer rl) {
      rl.add(backgroundLayer);
      rl.add(drawLayers);
   }

   void addCanvasRenderable(Renderable<Canvas> c) {
      addItem(Item.canvasItem(c));
   }

   void addSurfaceRenderable(Renderable<Surface> s) {
      addItem(Item.surfaceItem(s));
   }
   
   // TODO: remove items/renderables

   public void setPointerViewPosition(float x, float y) {
      pointerViewPosition.set(x, y);
   }

   public Point getPointerWorldPosition() {
      return camera.viewToWorld(pointerViewPosition);
   }

   private <R> void addItem(Item<R> item) {
      if (!renderables.add(item)) {
         // already exists in the set
         return;
      }
      
      Item<?> below = renderables.lower(item);
      if (below != null && below.getType() == item.getType()) {
         // join as tail of existing group
         @SuppressWarnings("unchecked")
         RenderableGroup<R> gg = (RenderableGroup<R>) below.getGroup();
         item.setGroup(gg);
         return;
      }
      
      Item<?> above = renderables.higher(item);
      if (above != null && above.getType() == item.getType()) {
         // join as head of existing group
         @SuppressWarnings("unchecked")
         RenderableGroup<R> gg = (RenderableGroup<R>) above.getGroup();
         item.setGroup(gg);
         gg.setFirst(item); // move this back to encompass new item
         return;
      }
      
      // create new group
      isDirty = true;
      if (item.getType() == Canvas.class) {
         Canvas canvas = g.createCanvas(g.viewSize).save();
         @SuppressWarnings("unchecked")
         RenderableGroup<R> gg = (RenderableGroup<R>) new CanvasGroup(g, canvas);
         item.setGroup(gg);
         gg.setFirst(item);
      } else if (item.getType() == Surface.class) {
         @SuppressWarnings("unchecked")
         RenderableGroup<R> gg = (RenderableGroup<R>) new SurfaceGroup();
         item.setGroup(gg);
         gg.setFirst(item);
      }
   }

   public void paint() {
      camera.setX(vehicle.getCarBody().getWorldCenter().x);
      background.update(camera);

      if (!isDirty) {
         // layers already created and will paint themselves
         return;
      }
      
      // (re)create draw layers
      RenderableGroup<?> lastGroup = null;
      int depth = 0;
      for (Item<?> item : renderables) {
         RenderableGroup<?> g = item.getGroup();
         if (g != lastGroup) {
            Layer l = g.asLayer();
            l.setDepth(depth++);
            if (l.parent() == null) {
               drawLayers.add(l);
            }
            lastGroup = g;
         }
      }
   }
}
