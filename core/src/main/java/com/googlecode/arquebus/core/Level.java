package com.googlecode.arquebus.core;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.json;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Gradient;
import playn.core.Json;
import playn.core.Layer;
import playn.core.util.Callback;

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
    
    public static void loadLevel(int levelNumber, Callback<Level> callback) {
      loadFromAsset("level-" + levelNumber + ".json", callback);
    }
    
    private static void loadFromAsset(String assetPath, final Callback<Level> level) {
      assets().getText(assetPath, new Callback<String>() {
        @Override
        public void onSuccess(String result) {
          level.onSuccess(fromJson(json().parse(result)));
        }

        @Override
        public void onFailure(Throwable cause) {
          level.onFailure(cause);
        }
      });
    }
    
    private static Level fromJson(Json.Object o) {
      Builder b = new Builder();
      // TODO
      return b.build();
    }
  }

  class Builder {
    // TODO
    public Level build() {
      return new LevelImpl(this);
    }

    private static class LevelImpl implements Level {
      
      LevelImpl(Builder builder) {
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
        
        Gradient gradient = graphics().createLinearGradient(0, graphics().height(), 0, 0,
            new int[] { Color.rgb(0x80, 0xa0, 0xff), Color.rgb(0x20, 0x80, 0xff) }, new float[] { 0, 1 });
        CanvasImage canvasImage = graphics().createImage(graphics().width(), graphics().height());
        Canvas canvas = canvasImage.canvas();
        canvas.setFillGradient(gradient);
        canvas.fillRect(0, 0, graphics().width(), graphics().height());
        Layer layer = graphics().createImageLayer().setImage(canvasImage);
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
