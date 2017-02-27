package com.bluegosling.arquebus.core.model;

import com.bluegosling.arquebus.core.jbox2d.ContactListeners;
import com.bluegosling.arquebus.core.jbox2d.ContactListeners.BodyListener;
import com.google.common.collect.Lists;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import playn.core.Clock;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class ArtilleryModel {

   public static class WeaponDef {
      // TODO ...
   }

   private static Damageable NO_OP = new Damageable() {
      @Override
      public float damageCapacity() {
         return Float.MAX_VALUE;
      }

      @Override
      public void applyDamage(float amount) {
      }
   };

   public static class Hit {
      private final Damageable target;
      private final Vec2 hitLocation;
      private final float damage;

      public Hit(Damageable target, Vec2 hitLocation, float damage) {
         this.target = target;
         this.hitLocation = hitLocation;
         this.damage = damage;
      }
   }

   protected final VehicleModel vehicle;
   protected final Vec2 lastPos;
   protected Vec2 detonationPos;
   protected int detonationMillis = -1;
   protected float blastRadius = -1;

   public ArtilleryModel(VehicleModel vehicle, Vec2 pos) {
      this.vehicle = vehicle;
      lastPos = new Vec2(pos);
   }

   protected abstract Vec2 getPosition();

   protected abstract boolean shouldDestroy(int tick);

   protected abstract boolean detonate(Hit lastHit);

   protected abstract float contactBlastRadius();

   protected abstract List<Hit> computeHits();

   protected abstract float getBlastRadius(float previousRadius, int delta);

   protected abstract float getBlastRadiusDamage(float distSquared);

   boolean update(Clock clock) {
      int delta = clock.dt;
      if (detonationPos == null) {
         Vec2 newPos = getPosition();
         List<Hit> hits = computeHits();
         Hit hitsArray[] = hits.toArray(new Hit[hits.size()]);
         final Vec2 diff = new Vec2();
         Arrays.sort(hitsArray, new Comparator<Hit>() {
            @Override
            public int compare(Hit o1, Hit o2) {
               diff.set(o1.hitLocation).sub(lastPos);
               float l1 = diff.lengthSquared();
               diff.set(o2.hitLocation).sub(lastPos);
               float l2 = diff.lengthSquared();
               return (int) Math.signum(l1 - l2);
            }
         });
         for (Hit hit : hitsArray) {
            hit.target.applyDamage(hit.damage);
            if (detonate(hit)) {
               newPos = detonationPos = hit.hitLocation;
               detonationMillis = clock.tick;
               blastRadius = contactBlastRadius();
               delta = 0;
               break;
            }
         }
         lastPos.set(newPos);
      }
      if (detonationPos != null) {
         blastRadius = getBlastRadius(blastRadius, delta);
         // TODO: compute bounding box for blast radius, find intersecting fixtures, identify ones
         // that are *in* blast radius, and apply damage
      }
      return !shouldDestroy(clock.tick);
   }

   public static class Bullet extends ArtilleryModel {
      private Vec2 target;
      private final float damage;

      public Bullet(VehicleModel vehicle, Vec2 start, Vec2 target, float damage) {
         super(vehicle, start);
         this.target = target;
         this.damage = damage;
      }

      @Override
      public Vec2 getPosition() {
         return lastPos;
      }

      public Vec2 getTarget() {
         return target;
      }

      @Override
      protected boolean shouldDestroy(int tick) {
         return true;
      }

      @Override
      protected boolean detonate(Hit lastHit) {
         return false;
      }

      @Override
      protected float contactBlastRadius() {
         return 0;
      }

      @Override
      protected List<Hit> computeHits() {
         final List<Hit> hits = new LinkedList<Hit>();
         vehicle.getCarBody().m_world.raycast(new RayCastCallback() {
            float damageLeft = damage;

            @Override
            public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
               Object o = fixture.m_body.m_userData;
               if (o instanceof Damageable) {
                  Damageable d = (Damageable) o;
                  float currentDamage = Math.min(d.damageCapacity(), damageLeft);
                  hits.add(new Hit(d, point, currentDamage));
                  damageLeft -= currentDamage;
                  if (damageLeft <= 0) {
                     // new terminal point
                     target = point;
                     return 0;
                  }
                  return -1;
               } else {
                  return 0;
               }
            }
         }, lastPos, target);
         return hits;
      }

      @Override
      protected float getBlastRadius(float previousRadius, int delta) {
         return 0;
      }

      @Override
      protected float getBlastRadiusDamage(float distSquared) {
         return 0;
      }
   }

   public static class MissileExplosion {
      public static class Builder {
         private float initialRadius;
         private float maximumRadius;
         private int expansionMillis;
         private int lingerMillis;
         private float collisionDamage;
         private float blastDamage;

         private Builder() {
         }

         public Builder startingRadius(float initialRadius) {
            this.initialRadius = initialRadius;
            return this;
         }

         public Builder endingRadius(float maximumRadius) {
            this.maximumRadius = maximumRadius;
            return this;
         }

         public Builder expansionTime(int expansionMillis) {
            this.expansionMillis = expansionMillis;
            return this;
         }

         public Builder lingerTime(int lingerMillis) {
            this.lingerMillis = lingerMillis;
            return this;
         }

         public Builder collisionDamage(float collisionDamage) {
            this.collisionDamage = collisionDamage;
            return this;
         }

         public Builder blastDamage(float blastDamage) {
            this.blastDamage = blastDamage;
            return this;
         }

         public MissileExplosion build() {
            return new MissileExplosion(this);
         }
      }

      public static Builder newBuilder() {
         return new Builder();
      }

      private final float initialRadius;
      private final float maximumRadius;
      private final float maximumRadiusSq;
      private final int expansionMillis;
      private final int lingerMillis;
      private final float collisionDamage;
      private final float blastDamage;

      private MissileExplosion(Builder builder) {
         this.initialRadius = builder.initialRadius;
         this.maximumRadius = builder.maximumRadius;
         this.maximumRadiusSq = maximumRadius * maximumRadius;
         this.expansionMillis = builder.expansionMillis;
         this.lingerMillis = builder.lingerMillis;
         this.collisionDamage = builder.collisionDamage;
         this.blastDamage = builder.blastDamage;
      }
   }

   public static class Missile extends ArtilleryModel {
      private static final float MAX_DISTANCE = 1000; // 1 km
      private static final float MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

      private final Body body;
      private boolean removed;
      private final MissileExplosion explosion;
      private final List<Hit> hits = Lists.newLinkedList();

      public Missile(VehicleModel vehicle, Body body, final MissileExplosion explosion) {
         super(vehicle, body.getWorldCenter());
         this.body = body;
         this.explosion = explosion;
         ContactListeners.addBodyListener(body, new BodyListener() {
            @Override
            public void endContact(Contact contact, Body body, Body other) {
            }

            @Override
            public void beginContact(Contact contact, Body body, Body other) {
               Object o = other.getUserData();
               WorldManifold manifold = new WorldManifold();
               contact.getWorldManifold(manifold);
               Vec2 pos = new Vec2(manifold.points[0]);
               if (o instanceof Damageable) {
                  hits.add(new Hit((Damageable) o, pos, explosion.collisionDamage));
               } else {
                  // TODO: support warping of ground, like creating pot-holes on impact
                  hits.add(new Hit(NO_OP, pos, explosion.collisionDamage));
               }
            }
         });
      }

      @Override
      protected Vec2 getPosition() {
         return body.getWorldCenter();
      }

      @Override
      protected boolean shouldDestroy(int tick) {
         return getPosition().sub(vehicle.getCarBody().getWorldCenter())
               .lengthSquared() > MAX_DISTANCE_SQ
               || (detonationMillis != -1 && tick > detonationMillis
                     + explosion.expansionMillis + explosion.lingerMillis);
      }

      @Override
      protected boolean detonate(Hit lastHit) {
         // remove body on detonation
         remove();
         return true;
      }

      private void remove() {
         if (!removed) {
            body.m_world.destroyBody(body);
            removed = true;
         }
      }

      @Override
      protected float contactBlastRadius() {
         return explosion.initialRadius;
      }

      @Override
      protected List<Hit> computeHits() {
         return hits;
      }

      @Override
      protected float getBlastRadius(float previousRadius, int delta) {
         float factor =
               (explosion.maximumRadius - explosion.initialRadius) / explosion.expansionMillis;
         return Math.min(previousRadius + delta * factor, explosion.maximumRadius);
      }

      @Override
      protected float getBlastRadiusDamage(float distSquared) {
         float factor =
               Math.max(0, explosion.maximumRadiusSq - distSquared) / explosion.maximumRadiusSq;
         return factor * explosion.blastDamage;
      }

      @Override
      boolean update(Clock clock) {
         boolean ret = super.update(clock);
         hits.clear();
         if (!ret) {
            remove();
         }
         return ret;
      }
   }
}
