package com.googlecode.arquebus.core.jbox2d;

import com.google.common.collect.Sets;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import playn.core.Asserts;

import java.util.Set;

/**
 * Helper methods for using {@link ContactListener}s. Use {@link #init(World)} to enable a world to
 * accept multiple listeners (it only allows a single listener by default). Then you can use the
 * various methods herein to add and remove listeners.
 *
 * @author Joshua Humphries (jhumphries131@gmail.com)
 */
public final class ContactListeners {
  private ContactListeners() {
  }
  
  /**
   * Simple contact listener. This excludes the less-frequently used methods of
   * {@link ContactListener} so implementing it requires less boiler-plate.
   *
   * @author Joshua Humphries (jhumphries131@gmail.com)
   */
  public static interface SimpleListener {
    void beginContact(Contact contact);
    void endContact(Contact contact);
  }

  /**
   * Contact listener for a body. With this interface, you can easily {@linkplain
   * ContactListeners#addBodyListener(Body, BodyListener) listen for contacts involving a specified
   * body}. Like {@link SimpleListener}, it excludes infrequently-used methods of 
   * {@link ContactListener}. When this listener is invoked, the {@code body} parameter is the body
   * that we're listening for, and {@code other} is the other body with which it is in contact.
   *
   * @author Joshua Humphries (jhumphries131@gmail.com)
   */
  public static interface BodyListener {
    void beginContact(Contact contact, Body body, Body other);
    void endContact(Contact contact, Body body, Body other);
  }

  private static class MultiListener implements ContactListener {
    private final Set<ContactListener> listeners = Sets.newHashSet();
    
    public boolean addListener(ContactListener listener) {
      return listeners.add(listener);
    }
    
    public boolean removeListener(ContactListener listener) {
      return listeners.remove(listener);
    }

    @Override
    public void beginContact(Contact contact) {
      for (ContactListener listener : listeners) {
        listener.beginContact(contact);
      }
    }

    @Override
    public void endContact(Contact contact) {
      for (ContactListener listener : listeners) {
        listener.endContact(contact);
      }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
      for (ContactListener listener : listeners) {
        listener.preSolve(contact, oldManifold);
      }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
      for (ContactListener listener : listeners) {
        listener.postSolve(contact, impulse);
      }
    }
  }
  
  private static class SimpleListenerAdapter implements ContactListener {
    private final SimpleListener listener;
    
    public SimpleListenerAdapter(SimpleListener listener) {
      this.listener = Asserts.checkNotNull(listener);
    }

    @Override
    public void beginContact(Contact contact) {
      listener.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
      listener.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
    
    @Override
    public int hashCode() {
      return listener.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if (o instanceof SimpleListenerAdapter) {
        SimpleListenerAdapter other = (SimpleListenerAdapter) o;
        return listener.equals(other.listener);
      }
      return false;
    }
  }
  
  private static class BodyListenerAdapter implements ContactListener {
    private final BodyListener listener;
    private final Body body;
    
    public BodyListenerAdapter(BodyListener listener, Body body) {
      this.listener = Asserts.checkNotNull(listener);
      this.body = Asserts.checkNotNull(body);
    }

    private Body[] getBodies(Contact contact) {
      if (contact.getFixtureA().getBody() == body) {
        return new Body[] { body, contact.getFixtureB().getBody() };
      }
      if (contact.getFixtureB().getBody() == body) {
        return new Body[] { body, contact.getFixtureA().getBody() };
      }
      return null;
    }
    
    @Override
    public void beginContact(Contact contact) {
      Body bodies[] = getBodies(contact);
      if (bodies != null) {
        listener.beginContact(contact, bodies[0], bodies[1]);
      }
    }

    @Override
    public void endContact(Contact contact) {
      Body bodies[] = getBodies(contact);
      if (bodies != null) {
        listener.endContact(contact, bodies[0], bodies[1]);
      }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
    
    @Override
    public int hashCode() {
      return listener.hashCode() ^ body.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if (o instanceof SimpleListenerAdapter) {
        SimpleListenerAdapter other = (SimpleListenerAdapter) o;
        return listener.equals(other.listener);
      }
      return false;
    }
  }
  
  public static void init(World world) {
    ContactListener listener = world.getContactManager().m_contactListener;
    if (listener instanceof MultiListener) {
      return;
    }
    
    world.setContactListener(new MultiListener());
    if (listener != null) {
      addContactListener(world, listener);
    }
  }
  
  public static boolean addContactListener(World world, ContactListener listener) {
    MultiListener multi = (MultiListener) world.getContactManager().m_contactListener;
    return multi.addListener(Asserts.checkNotNull(listener));
  }

  public static boolean removeContactListener(World world, ContactListener listener) {
    if (listener == null) {
      return false;
    }
    MultiListener multi = (MultiListener) world.getContactManager().m_contactListener;
    return multi.removeListener(listener);
  }

  public static boolean addSimpleListener(World world, SimpleListener listener) {
    return addContactListener(world, new SimpleListenerAdapter(listener));
  }

  public static boolean removeSimpleListener(World world, SimpleListener listener) {
    if (listener == null) {
      return false;
    }
    return removeContactListener(world, new SimpleListenerAdapter(listener));
  }
  
  public static boolean addBodyListener(Body body, BodyListener listener) {
    return addContactListener(body.getWorld(), new BodyListenerAdapter(listener, body));
  }

  public static boolean removeBodyListener(Body body, BodyListener listener) {
    if (listener == null || body == null) {
      return false;
    }
    return removeContactListener(body.getWorld(), new BodyListenerAdapter(listener, body));
  }

}
