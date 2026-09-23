package com.infiniteconquest.gui;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Deterministic visual fixture runner used by CI under Xvfb. */
public final class GuiScreenshotHarness {
    private static final String[] STATIC_SCENARIOS = {"opening-board", "selected-hand", "expanded-hand"};

    private GuiScreenshotHarness() { }

    public static void main(String[] args) throws Exception {
        Path outputDirectory = Path.of(args.length == 0 ? "build/screenshots" : args[0]);
        for (String scenario : STATIC_SCENARIOS) {
            SwingUtilities.invokeAndWait(() -> capture(outputDirectory, scenario));
        }
        captureMotion(outputDirectory, "deployment-motion", 145);
        captureMotion(outputDirectory, "invalid-drop-motion", 90);
        captureMotion(outputDirectory, "board-movement", 160);
        captureMotion(outputDirectory, "melee-lunge", 180);
        System.exit(0);
    }

    private static void capture(Path outputDirectory, String scenario) {
        InfiniteConquestGui gui = new InfiniteConquestGui(true);
        try {
            prepare(gui, scenario);
            gui.captureScreenshot(outputDirectory.resolve(scenario + ".png"));
        } finally {
            gui.dispose();
        }
    }

    private static void captureMotion(Path outputDirectory, String scenario, int delayMs) throws Exception {
        CountDownLatch captured = new CountDownLatch(1);
        SwingUtilities.invokeAndWait(() -> {
            InfiniteConquestGui gui = new InfiniteConquestGui(true);
            prepare(gui, scenario);
            Timer midpoint = new Timer(delayMs, event -> {
                try {
                    gui.captureScreenshot(outputDirectory.resolve(scenario + ".png"));
                } finally {
                    gui.dispose();
                    captured.countDown();
                }
            });
            midpoint.setRepeats(false);
            midpoint.start();
        });
        if (!captured.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timed out capturing " + scenario);
        }
    }

    private static void prepare(InfiniteConquestGui gui, String scenario) {
        gui.setSize(1500, 980);
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
        gui.prepareScreenshotScenario(scenario);
    }
}
