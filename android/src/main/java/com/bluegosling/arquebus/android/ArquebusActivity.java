package com.bluegosling.arquebus.android;

import playn.android.GameActivity;

import com.bluegosling.arquebus.core.Arquebus;

public class ArquebusActivity extends GameActivity {

  @Override public void main () {
    new Arquebus(platform());
  }
}
