package com.googlecode.arquebus.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import com.googlecode.arquebus.core.ArquebusMain;

public class ArquebusMainActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new ArquebusMain());
  }
}
