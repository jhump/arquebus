package com.googlecode.arquebus.core;

import org.jbox2d.testbed.framework.TestbedController;
import org.jbox2d.testbed.framework.TestbedController.MouseBehavior;
import org.jbox2d.testbed.framework.TestbedController.UpdateBehavior;
import org.jbox2d.testbed.framework.TestbedErrorHandler;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.j2d.DebugDrawJ2D;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import org.jbox2d.testbed.framework.j2d.TestbedSidePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * The entry point for the testbed application.
 */
public class ArquebusJBox2DTestbed {
  private static final Logger log = LoggerFactory.getLogger(ArquebusJBox2DTestbed.class);

  public static void main(String[] args) {
    TestbedModel model = new TestbedModel();
    final TestbedController controller =
        new TestbedController(model, UpdateBehavior.UPDATE_CALLED, MouseBehavior.NORMAL,
            new TestbedErrorHandler() {
              @Override
              public void serializationError(Exception e, String message) {
                JOptionPane.showMessageDialog(null, message, "Serialization Error",
                    JOptionPane.ERROR_MESSAGE);
              }
            });
    TestPanelJ2D panel = new TestPanelJ2D(model, controller);
    model.setPanel(panel);
    model.setDebugDraw(new DebugDrawJ2D(panel, true));
    model.addTest(new ArquebusJBox2DTest());

    JFrame testbed = new JFrame();
    testbed.setTitle("Arquebus JBox2D Testbed");
    testbed.setLayout(new BorderLayout());
    TestbedSidePanel side = new TestbedSidePanel(model, controller);
    testbed.add(panel, "Center");
    testbed.add(new JScrollPane(side), "East");
    testbed.pack();
    testbed.setVisible(true);
    testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        controller.playTest(0);
        controller.start();
      }
    });
  }
}
