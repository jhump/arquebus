package com.googlecode.arquebus.core.model;

import com.google.common.collect.Maps;
import com.googlecode.arquebus.core.Level;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import playn.core.Asserts;
import pythagoras.f.Line;
import pythagoras.f.Point;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class GroundModel {
  private static final int MINIMUM_EXTENTS = 400;
  private static final float WORLD_SCALE_FACTOR = 6;
  
  private static final int MAX_HEIGHT = 40;
  private static final int MIN_HEIGHT = 5;
  
  private static final int MAX_RUN_LENGTH = 30;
  private static final int MIN_RUN_LENGTH = 3;
  
  private static final double MAX_GROUND_SLOPE = 0.6;

  // these can be re-used, so no need to allocate extra unnecessary objects
  private static final EdgeShape EDGE = new EdgeShape();
  private static final FixtureDef FD = new FixtureDef() {{
    shape = EDGE;
    density = 0;
  }};

  public class Segment {
    final Line line;
    final Fixture fixture;
    
    Segment(Line line) {
      this.line = line;
      EDGE.set(new Vec2(line.x1 / WORLD_SCALE_FACTOR, line.y1 / WORLD_SCALE_FACTOR),
          new Vec2(line.x2 / WORLD_SCALE_FACTOR, line.y2 / WORLD_SCALE_FACTOR));
      this.fixture = ground.createFixture(FD);
      this.fixture.setFriction(friction);
    }
    
    public Line getLine() {
      return line;
    }
    
    public Fixture getFixture() {
      return fixture;
    }
  }

  private final Random rand = new Random();
  private final Body ground;
  private final int seed;
  private final float jaggedness;
  private final float heightVariance;
  private final float friction;
  private final TreeMap<Integer, Integer> points = Maps.newTreeMap();
  private final TreeMap<Integer, Segment> segments = Maps.newTreeMap();
  private int minChunk;
  private int maxChunk;
  private float minX;
  private float maxX;
  
  GroundModel(World world, Level level) {
    this.seed = level.getSeed();
    this.jaggedness = level.getGroundJaggedness();
    this.heightVariance = level.getGroundHeightVariance();
    this.friction = level.getGroundFriction();
    ground = world.createBody(new BodyDef());
    init();
  }
  
  private void seed(int chunk) {
    rand.setSeed(seed + chunk * 17);
    rand.setSeed(rand.nextLong());
  }
  
  private int randomHeight(int runLength, int adjacentHeight) {
    double slope = rand.nextDouble() * MAX_GROUND_SLOPE + 0.001;
    if (adjacentHeight == MAX_HEIGHT || (adjacentHeight > MIN_HEIGHT && rand.nextBoolean())) {
      slope *= -1;
    }
    int newHeight = (int) (slope * runLength + adjacentHeight);
    if (newHeight == adjacentHeight) {
      if (slope > 0) {
        newHeight++;
      } else {
        newHeight--;
      }
    }
    if (newHeight > MAX_HEIGHT) {
      return MAX_HEIGHT;
    } else if (newHeight < MIN_HEIGHT) {
      return MIN_HEIGHT;
    }
    return newHeight;
  }
  
  private void init() {
    seed(0);
    generateChunkZero();
    minChunk = maxChunk = 0;
  }
  
  private void generateRight(int priorX, int priorY, int minX, int maxX) {
    int minLength = minX - priorX;
    if (minLength < MIN_RUN_LENGTH) {
      minLength = MIN_RUN_LENGTH;
    }
    int runLength = rand.nextInt(MAX_RUN_LENGTH - minLength) + minLength;
    int x = priorX + runLength;
    while (x <= maxX) {
      int y = randomHeight(runLength, priorY);
      points.put(x,  y);
      runLength = rand.nextInt(MAX_RUN_LENGTH - minLength) + minLength;
      int tmp = x;
      x = x + runLength;
      priorX = tmp;
      priorY = y;
    }
  }

  private void generateLeft(int priorX, int priorY, int minX, int maxX) {
    int minLength = priorX - maxX;
    if (minLength < MIN_RUN_LENGTH) {
      minLength = MIN_RUN_LENGTH;
    }
    int runLength = rand.nextInt(MAX_RUN_LENGTH - minLength) + minLength;
    int x = priorX - runLength;
    while (x >= minX) {
      int y = randomHeight(runLength, priorY);
      points.put(x,  y);
      runLength = rand.nextInt(MAX_RUN_LENGTH - minLength) + minLength;
      int tmp = x;
      x = x - runLength;
      priorX = tmp;
      priorY = y;
    }
  }
  
  private void generateChunkZero() {
    // special case the very first chunk
    int initRun = rand.nextInt(MAX_RUN_LENGTH - MIN_RUN_LENGTH) + MIN_RUN_LENGTH;
    int x0 = 50 - initRun / 2;
    int y0 = rand.nextInt(MAX_HEIGHT - MIN_HEIGHT) + MIN_HEIGHT;
    // put initial segment
    points.put(x0, y0);
    int x1 = x0 + initRun;
    int y1 = randomHeight(initRun, y0);
    points.put(x1, y1);
    // generate more segments to the right
    generateRight(x1, y1, x1, 100);
    // and then to the left
    generateLeft(x0, y0, 0, x0);
  }

  private void generateChunk(int chunk) {
    seed(chunk);
    
    if (chunk > maxChunk) {
      if (chunk == 0) {
        generateChunkZero();
      } else {
        Map.Entry<Integer, Integer> start = points.lastEntry();
        generateRight(start.getKey(), start.getValue(), chunk * 100, (chunk + 1) * 100);
      }
      maxChunk = chunk;
    } else if (chunk < minChunk) {
      if (chunk == 0) {
        generateChunkZero();
      } else {
        Map.Entry<Integer, Integer> start = points.firstEntry();
        generateLeft(start.getKey(), start.getValue(), chunk * 100, (chunk + 1) * 100);
      }
      minChunk = chunk;
    } else {
      throw new IllegalArgumentException("Chunk #" + chunk + " already generated");
    }
  }
  
  private void extend(int lo, int hi) {
    while (lo < points.firstKey()) {
      generateChunk(minChunk - 1);
    }
    while (hi > points.lastKey()) {
      generateChunk(maxChunk + 1);
    }
  }
  
  private void addSegments(int min, int max) {
    Iterator<Map.Entry<Integer, Integer>> iter =
          points.subMap(min, true, max, true).entrySet().iterator();
    Map.Entry<Integer, Integer> p1 = iter.next();
    while (iter.hasNext()) {
      Map.Entry<Integer, Integer> p2 = iter.next();
      Line line =
          new Line(new Point(p1.getKey(), p1.getValue()), new Point(p2.getKey(), p2.getValue()));
      if (!segments.containsKey(p1.getKey())) {
        segments.put(p1.getKey(), new Segment(line));
      }
      // TODO: log/warn if segment exists?
      p1 = p2;
    }
  }
  
  private void clearSegments(Map<Integer, Segment> submap) {
    Iterator<Map.Entry<Integer, Segment>> iter = submap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Integer, Segment> entry = iter.next();
      iter.remove();
      ground.destroyFixture(entry.getValue().getFixture());
    }
  }
  
  private void removeLeadingSegments(int min) {
    clearSegments(segments.headMap(min, false));
  }

  private void removeTrailingSegments(int max) {
    clearSegments(segments.tailMap(max, false));
  }

  public void setVisibleExtents(float minX, float maxX) {
    Asserts.checkArgument(maxX > minX);
    this.minX = minX;
    this.maxX = maxX;
    
    if (maxX - minX < MINIMUM_EXTENTS) {
      float center = (minX + maxX) / 2;
      minX = center - MINIMUM_EXTENTS / 2;
      maxX = center + MINIMUM_EXTENTS / 2;
    }
    int min = (int) (minX * WORLD_SCALE_FACTOR);
    int max = (int) (maxX * WORLD_SCALE_FACTOR);
    
    extend(min, max);
    int minL = points.floorKey(min);
    int maxR = points.ceilingKey(max);
    int maxL = points.lowerKey(maxR);
    
    if (segments.isEmpty()) {
      addSegments(minL, maxR);
      return;
    }
    
    int minKey = segments.firstKey();
    if (minL > minKey) {
      removeLeadingSegments(minL);
    } else if (minL < minKey) {
      addSegments(minL, minKey);
    }

    int maxKey = segments.lastKey();
    if (maxL > maxKey) {
      addSegments(maxKey + 1, maxR);
    } else if (maxL < maxKey) {
      removeTrailingSegments(maxL);
    }
  }
  
  public Iterable<Point> visiblePoints() {
    int lo = (int) Math.floor(minX * WORLD_SCALE_FACTOR);
    int hi = (int) Math.ceil(maxX * WORLD_SCALE_FACTOR);
    final Collection<Map.Entry<Integer, Integer>> entries =
        points.subMap(points.floorKey(lo), true, points.ceilingKey(hi), true).entrySet();
    return new Iterable<Point>() {
      @Override
      public Iterator<Point> iterator() {
        return new Iterator<Point>() {
          private final Iterator<Map.Entry<Integer, Integer>> iter = entries.iterator();
          
          @Override
          public boolean hasNext() {
            return iter.hasNext();
          }

          @Override
          public Point next() {
            Map.Entry<Integer, Integer> entry = iter.next();
            return new Point(entry.getKey()  / WORLD_SCALE_FACTOR,
                entry.getValue() / WORLD_SCALE_FACTOR);
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  public Iterable<Segment> segments() {
    return Collections.unmodifiableCollection(segments.values());
  }
  
  public Body getBody() {
    return ground;
  }
}
