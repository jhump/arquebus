package com.bluegosling.arquebus.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import playn.core.Canvas;
import playn.core.Color;
import playn.core.Gradient;
import playn.core.Graphics;
import playn.core.Json;
import playn.core.Platform;
import playn.scene.CanvasLayer;
import playn.scene.Layer;
import pythagoras.f.IDimension;
import react.SignalView.Listener;
import react.Try;

public interface Level {
  int levelNumber();
  
  // background layers
  Multimap<Float, Layer> getBackgroundLayers();
  
  // ground generation
  int getSeed();
  float getGroundJaggedness();
  float getGroundHeightVariance();
  float getGroundFriction();
  float getGravity();
  
  // default vehicle and its weapons
  //TODO
  
  // enemies
  //TODO
  
  final class Loader {
    private Loader() {
    }
    
    public static void loadLevel(Platform plat, int levelNumber, Listener<Try<Level>> listener) {
      loadFromAsset(plat, "level-" + levelNumber + ".json", listener);
    }
    
    private static void loadFromAsset(final Platform plat, String assetPath,
    		final Listener<Try<Level>> listener) {
    	plat.assets().getText(assetPath).onComplete(new Listener<Try<String>>() {
			@Override
			public void onEmit(Try<String> event) {
				if (event.isFailure()) {
					listener.onEmit(Try.<Level>failure(event.getFailure()));
				} else {
					listener.onEmit(Try.success(fromJson(plat, plat.json().parse(event.get()))));
				}
			}
    	});
    }
    
    private static Level fromJson(Platform plat, Json.Object o) {
      Builder b = new Builder();
      // TODO
      return b.build(plat);
    }
  }

  class Builder {
    // TODO
    public Level build(Platform plat) {
      return new LevelImpl(plat, this);
    }

    private static class LevelImpl implements Level {
      private final Graphics g;
      
      LevelImpl(Platform plat, Builder builder) {
    	  this.g = plat.graphics();
        // TODO
      }

      @Override
      public int levelNumber() {
        // TODO Auto-generated method stub
        return 1;
      }

      @Override
      public Multimap<Float, Layer> getBackgroundLayers() {
        // TODO Auto-generated method stub
        
        //Image bgImage = assets().getImage("images/bg.png");
        //ImageLayer bgLayer = graphics().createImageLayer(bgImage);
    	IDimension dim = g.viewSize;
        Canvas canvas = g.createCanvas(dim);
        Gradient gradient = canvas.createGradient(new Gradient.Linear(0, dim.height(), 0, 0,
              new int[] { Color.rgb(0x80, 0xa0, 0xff), Color.rgb(0x20, 0x80, 0xff) }, new float[] { 0, 1 }));
        canvas.setFillGradient(gradient);
        canvas.fillRect(0, 0, dim.width(), dim.height());
        Layer layer = new CanvasLayer(g, canvas);
        // can't use TreeMultimap because only keys are sorted, not values
        Multimap<Float, Layer> map = ArrayListMultimap.create();
        map.put(0f, layer); // one layer, no scrolling
        return map;
      }

      @Override
      public int getSeed() {
        // TODO Auto-generated method stub
        return 10000;
      }

      @Override
      public float getGroundJaggedness() {
        // TODO Auto-generated method stub
        return 0;
      }

      @Override
      public float getGroundHeightVariance() {
        // TODO Auto-generated method stub
        return 0;
      }

      @Override
      public float getGroundFriction() {
        // TODO Auto-generated method stub
        return 0.9f;
      }
      
      @Override
      public float getGravity() {
        // TODO Auto-generated method stub
        return 9.8f;
      }
    }
  }
}
