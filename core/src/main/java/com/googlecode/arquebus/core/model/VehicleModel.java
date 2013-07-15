package com.googlecode.arquebus.core.model;

import com.googlecode.arquebus.core.jbox2d.ContactListeners;
import com.googlecode.arquebus.core.jbox2d.ContactListeners.BodyListener;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;

import playn.core.PlayN;


public class VehicleModel {
  private static final Vec2 CAR_BODY_VERTICES[] = new Vec2[] {
    new Vec2(-1.6f, -0.2f),
    new Vec2(1.6f, -0.2f),
    new Vec2(2.33f, 0.1f),
    new Vec2(2.33f, 0.61f),
    new Vec2(0.8f, 1.37f),
    new Vec2(-1.2f, 1.37f),
    new Vec2(-2.37f, 0.8f),
    new Vec2(-2.37f, 0.1f)
  };
  private static final PolygonShape CAR_BODY = new PolygonShape() {{
    set(CAR_BODY_VERTICES, CAR_BODY_VERTICES.length);
  }};
  
  // TODO: add data for car canopy, bumper, etc. fixtures
  // TODO: figure out how to add movable guns/weapons as fixtures?
  
  private static final CircleShape WHEEL_SHAPE = new CircleShape() {{
    setRadius(0.6f);
  }};
  
  private static final Vec2 INITIAL_CAR_POS = new Vec2(0.0f, 8.0f);
  private static final Vec2 INITIAL_REAR_WHEEL_POS = new Vec2(-1.65f, 7.7f);
  private static final Vec2 INITIAL_FRONT_WHEEL_POS = new Vec2(1.65f, 7.7f);
  private static final float SUSPENSION_FREQ_HZ = 10;
  
  private static final float DEFAULT_TORQUE = 250;
  private static final float DEFAULT_TORQUE_SPLIT = 0.5f;
  private static final float DEFAULT_BRAKE_TORQUE = 100;
  private static final float DEFAULT_SUSPENSION_DAMPING = 1.7f;
  private static final float DEFAULT_FORWARD_SPEED = 200;
  private static final float DEFAULT_REVERSE_SPEED = 10;
  private static final float DEFAULT_DENSITY = 5;
  
  private static final int TICKS_UNTIL_AUTORESET = 500;

  public enum ThrottleDisposition {
    NEUTRAL, ACCELERATE, BRAKE
  }
  
  private final Body car;
  private final Body frontWheel;
  private final Body rearWheel;
  private final WheelJoint frontSpring;
  private final WheelJoint rearSpring;
  
  private final float torque;
  private final float torqueSplit;
  private final float brakeTorque;
  private final float suspensionDamping;
  private final float forwardSpeed;
  private final float reverseSpeed;
  private final float density;
  
  private float throttle;
  private float brake;
  private int rearWheelContactCount;
  private int frontWheelContactCount;
  private int carContactCount;

  private int ticksWhenVehicleStuck;
  
  VehicleModel(World world) {
    torque = DEFAULT_TORQUE;
    torqueSplit = DEFAULT_TORQUE_SPLIT;
    brakeTorque = DEFAULT_BRAKE_TORQUE;
    suspensionDamping = DEFAULT_SUSPENSION_DAMPING;
    forwardSpeed = DEFAULT_FORWARD_SPEED;
    reverseSpeed = DEFAULT_REVERSE_SPEED;
    density = DEFAULT_DENSITY;
    
    BodyDef bd = new BodyDef();
    bd.type = BodyType.DYNAMIC;
    bd.position.set(INITIAL_CAR_POS);
    bd.angularDamping = 1.0f;
    
    car = world.createBody(bd);
    car.createFixture(CAR_BODY, density);

    FixtureDef fd = new FixtureDef();
    fd.shape = WHEEL_SHAPE;
    fd.density = 1;
    fd.friction = 1;

    bd.position.set(INITIAL_REAR_WHEEL_POS);
    bd.angularDamping = 0;
    rearWheel = world.createBody(bd);
    rearWheel.createFixture(fd);

    bd.position.set(INITIAL_FRONT_WHEEL_POS);
    frontWheel = world.createBody(bd);
    frontWheel.createFixture(fd);

    WheelJointDef jd = new WheelJointDef();
    Vec2 axis = new Vec2(0.0f, 1.3f);
    jd.initialize(car, rearWheel, rearWheel.getPosition(), axis);
    jd.enableMotor = false;
    jd.frequencyHz = SUSPENSION_FREQ_HZ;
    jd.dampingRatio = suspensionDamping;
    rearSpring = (WheelJoint) world.createJoint(jd);

    jd.initialize(car, frontWheel, frontWheel.getPosition(), axis);
    frontSpring = (WheelJoint) world.createJoint(jd);
    
    ContactListeners.addBodyListener(car, new BodyListener() {
      @Override
      public void beginContact(Contact contact, Body body, Body other) {
        if (other != rearWheel && other != frontWheel) {
          carContactCount++;
        }
      }

      @Override
      public void endContact(Contact contact, Body body, Body other) {
        if (other != rearWheel && other != frontWheel) {
          carContactCount--;
        }
      }
    });

    ContactListeners.addBodyListener(frontWheel, new BodyListener() {
      @Override
      public void beginContact(Contact contact, Body body, Body other) {
        if (other != car) {
          frontWheelContactCount++;
          if (brake == 0 && throttle != 0) {
            applyThrottle(rearWheelContactCount > 0, frontWheelContactCount > 0);
          }
        }
      }

      @Override
      public void endContact(Contact contact, Body body, Body other) {
        if (other != car) {
          frontWheelContactCount--;
          if (brake == 0 && throttle != 0) {
            applyThrottle(rearWheelContactCount > 0, frontWheelContactCount > 0);
          }
        }
      }
    });
    
    ContactListeners.addBodyListener(rearWheel, new BodyListener() {
      @Override
      public void beginContact(Contact contact, Body body, Body other) {
        if (other != car) {
          rearWheelContactCount++;
          if (brake == 0 && throttle != 0) {
            applyThrottle(rearWheelContactCount > 0, frontWheelContactCount > 0);
          }
        }
      }

      @Override
      public void endContact(Contact contact, Body body, Body other) {
        if (other != car) {
          rearWheelContactCount--;
          if (brake == 0 && throttle != 0) {
            applyThrottle(rearWheelContactCount > 0, frontWheelContactCount > 0);
          }
        }
      }
    });
  }
  
  public void reset() {
    float carX = car.getWorldCenter().x;
    Vec2 v = new Vec2(carX, INITIAL_CAR_POS.y);
    car.setTransform(v, 0);
    car.setAngularVelocity(0);
    car.setLinearVelocity(new Vec2(0, 0));

    v = new Vec2(INITIAL_REAR_WHEEL_POS.x - INITIAL_CAR_POS.x + carX, INITIAL_REAR_WHEEL_POS.y);
    rearWheel.setTransform(v, 0);
    rearWheel.setAngularVelocity(0);
    rearWheel.setLinearVelocity(new Vec2(0, 0));

    v = new Vec2(INITIAL_FRONT_WHEEL_POS.x - INITIAL_CAR_POS.x + carX, INITIAL_FRONT_WHEEL_POS.y);
    frontWheel.setTransform(v, 0);
    frontWheel.setAngularVelocity(0);
    frontWheel.setLinearVelocity(new Vec2(0, 0));
    
    ticksWhenVehicleStuck = 0;
    applyThrottle();
  }
  
  public void update() {
    if (carTouching() && !rearWheelTouching() && !frontWheelTouching()
        && Math.abs(car.getAngularVelocity()) < 0.0001f
        && Math.abs(car.getLinearVelocity().x) < 0.0001f
        && Math.abs(car.getLinearVelocity().y) < 0.0001f) {
      if (ticksWhenVehicleStuck == 0) {
        ticksWhenVehicleStuck = PlayN.tick();
      } else if (ticksWhenVehicleStuck + TICKS_UNTIL_AUTORESET <= PlayN.tick()) {
        // reset car
        reset();
      }
    } else if (ticksWhenVehicleStuck != 0) {
      // vehicle unstuck itself w/out reset
      ticksWhenVehicleStuck = 0;
    }

    // when car comes to nearly complete stop, done braking
    if (brake != 0 && Math.abs(car.getLinearVelocity().x) < 0.1f
        && Math.abs(car.getLinearVelocity().y) < 0.1f) {
      brake = 0;
      applyThrottle();
    }
  }
  
  public void setThrottle(float throttle) {
    this.throttle = throttle;
    
    if (throttle != 0 && Math.signum(throttle) != Math.signum(car.getLinearVelocity().x)) {
      this.brake = Math.abs(throttle);
      applyBrake();
    } else {
      this.brake = 0;
      applyThrottle();
    }
  }

  private void applyBrake() {
    float effectiveBrakeTorque = brake * brakeTorque;
    
    rearSpring.enableMotor(true);
    rearSpring.setMaxMotorTorque(effectiveBrakeTorque);
    rearSpring.setMotorSpeed(0);
    frontSpring.enableMotor(true);
    frontSpring.setMaxMotorTorque(effectiveBrakeTorque);
    frontSpring.setMotorSpeed(0);
  }

  private void applyThrottle() {
    applyThrottle(rearWheelContactCount > 0, frontWheelContactCount > 0);
  }
  
  private void applyThrottle(boolean rear, boolean front) {
    float speed = throttle < 0 ? reverseSpeed : -forwardSpeed;
    float rearTorque = Math.abs(throttle) * torque * torqueSplit;
    float frontTorque = Math.abs(throttle) * torque * (1 - torqueSplit);

    if (rearTorque != 0 && rear) {
      rearSpring.enableMotor(true);
      rearSpring.setMaxMotorTorque(rearTorque);
      rearSpring.setMotorSpeed(speed);
    } else {
      rearTorque = 0;
      rearSpring.enableMotor(false);
    }

    if (frontTorque != 0 && front) {
      frontSpring.enableMotor(true);
      frontSpring.setMaxMotorTorque(frontTorque);
      frontSpring.setMotorSpeed(speed);
    } else {
      frontTorque = 0;
      frontSpring.enableMotor(false);
    }
  }
  
  public boolean rearWheelTouching() {
    return rearWheelContactCount > 0;
  }

  public boolean frontWheelTouching() {
    return frontWheelContactCount > 0;
  }
  
  public boolean carTouching() {
    return carContactCount > 0;
  }
  
  public Body getCarBody() {
    return car;
  }
  
  public Body getRearWheelBody() {
    return rearWheel;
  }
  
  public Body getFrontWheelBody() {
    return frontWheel;
  }
  
  public Joint getRearSpringJoint() {
    return rearSpring;
  }
  
  public Joint getFrontSpringJoint() {
    return frontSpring;
  }
  
  public ThrottleDisposition getThrottleDisposition() {
    if (brake != 0) return ThrottleDisposition.BRAKE;
    return throttle == 0 ? ThrottleDisposition.NEUTRAL : ThrottleDisposition.ACCELERATE;
  }
}
