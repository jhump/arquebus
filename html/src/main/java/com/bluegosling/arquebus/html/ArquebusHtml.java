package com.bluegosling.arquebus.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import com.bluegosling.arquebus.core.Arquebus;

public class ArquebusHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("arquebus/");
    new Arquebus(plat);
    plat.start();
  }
}
