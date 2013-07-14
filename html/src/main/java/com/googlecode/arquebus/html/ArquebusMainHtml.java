package com.googlecode.arquebus.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import com.googlecode.arquebus.core.ArquebusMain;

public class ArquebusMainHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform platform = HtmlPlatform.register(config);
    platform.assets().setPathPrefix("arquebus/");
    PlayN.run(new ArquebusMain());
  }
}
