package com.googlecode.arquebus.core.view;

import com.google.common.collect.Maps;
import com.googlecode.arquebus.core.model.ArtilleryModel;
import com.googlecode.arquebus.core.model.ArtilleryModel.Bullet;
import com.googlecode.arquebus.core.model.ArtilleryModel.Missile;
import com.googlecode.arquebus.core.model.GameModel;

import playn.core.Canvas;

import java.util.Map;


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
       
      // TODO Auto-generated method stub
      
    }
  }

  private static class MissileRenderer implements Renderer<Missile> {
    @Override
    public void render(Missile m, Canvas canvas, Camera camera) {
      // TODO Auto-generated method stub
        
    }
  }
}
