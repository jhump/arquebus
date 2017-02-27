package com.bluegosling.arquebus.core.view;

import com.bluegosling.arquebus.core.model.ArtilleryModel;
import com.bluegosling.arquebus.core.model.GameModel;
import com.bluegosling.arquebus.core.model.ArtilleryModel.Bullet;
import com.bluegosling.arquebus.core.model.ArtilleryModel.Missile;
import com.google.common.collect.Maps;
import playn.core.Canvas;
import playn.core.Color;
import pythagoras.f.Point;

import java.util.Map;

import org.jbox2d.common.Vec2;


public class ArtilleryView implements Renderable<Canvas> {
  
  public interface Renderer<A extends ArtilleryModel> {
    void render(A a, Canvas canvas, Camera camera);
  }
  
  private static Map<Class<?>, Renderer<?>> renderers = Maps.newHashMap();
  static {
     registerRenderer(Bullet.class, new BulletRenderer());
     registerRenderer(Missile.class, new MissileRenderer());
  }
  
  static <A extends ArtilleryModel> void registerRenderer(Class<A> type,
        Renderer<A> renderer) {
     renderers.put(type, renderer);
  }
  
  @SuppressWarnings("unchecked")
  private static <A extends ArtilleryModel> Renderer<? super A> getRenderer(Class<A> type) {
    Renderer<?> renderer = renderers.get(type);
    if (renderer != null) {
      return (Renderer<A>) renderer;
    }
    for (Class<?> c = type.getSuperclass(); c != null; c = c.getSuperclass()) {
      renderer = renderers.get(c);
      if (renderer != null) {
        // save result of hierarchy lookup for faster access next time
        renderers.put(type, renderer);
        return (Renderer<? super A>) renderer;
      }
    }
    throw new IllegalArgumentException("specified artillery type cannot be rendered");
  }
  
  private final GameModel model;
  
  public ArtilleryView(GameModel model) {
    this.model = model;
  }

  @Override
  public int getZindex() {
    return 10;
  }

  @Override
  public void render(Canvas canvas, Camera camera) {
    for (ArtilleryModel a : model.getArtillery()) {
      doRender(a, canvas, camera);
    }
  }
  
  private <A extends ArtilleryModel> void doRender(A artillery, Canvas canvas, Camera camera) {
    @SuppressWarnings("unchecked")
    Renderer<? super A> r = getRenderer((Class<A>) artillery.getClass());
    r.render(artillery, canvas, camera);
  }
  
  private static class BulletRenderer implements Renderer<Bullet> {
    @Override
    public void render(Bullet b, Canvas canvas, Camera camera) {
       Vec2 originVec = b.getPosition();
       Vec2 endVec = b.getTarget();
       Point origin = camera.worldToView(new Point(originVec.x, originVec.y));
       Point end = camera.worldToView(new Point(endVec.x, endVec.y));
       canvas.setStrokeWidth(1)
             .setStrokeColor(Color.argb(80, 255, 255, 255))
             .drawLine(origin.x, origin.y, end.x, end.y);
       canvas.setFillColor(Color.argb(40, 255, 224, 100))
             .fillCircle(origin.x, origin.y, 20);
    }
  }

  private static class MissileRenderer implements Renderer<Missile> {
    @Override
    public void render(Missile m, Canvas canvas, Camera camera) {
      // TODO Auto-generated method stub
    }
  }
}
