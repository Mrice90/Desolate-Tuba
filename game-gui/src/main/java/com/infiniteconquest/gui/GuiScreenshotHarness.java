package com.infiniteconquest.gui;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Deterministic visual fixture runner used by CI under Xvfb. */
public final class GuiScreenshotHarness {
    private static final String[] STATIC_SCENARIOS = {"opening-board", "selected-hand", "expanded-hand", "crowded-board"};

    private GuiScreenshotHarness() { }

    public static void main(String[] args) throws Exception {
        Path outputDirectory = Path.of(args.length == 0 ? "build/screenshots" : args[0]);
        for (int[] size : new int[][] {{1280, 650}, {1366, 768}, {1920, 1080}, {1100, 700}}) {
            for (String scenario : STATIC_SCENARIOS) {
                SwingUtilities.invokeAndWait(() -> capture(outputDirectory, scenario, size[0], size[1]));
            }
        }
        captureMotion(outputDirectory, "deployment-motion", 145);
        captureMotion(outputDirectory, "invalid-drop-motion", 90);
        captureMotion(outputDirectory, "board-movement", 160);
        captureMotion(outputDirectory, "melee-lunge", 180);
        captureMotion(outputDirectory, "card-destruction", 150);
        SwingUtilities.invokeAndWait(() -> {
            InfiniteConquestGui gui = new InfiniteConquestGui(true);
            try {
                gui.captureOpeningScreens(outputDirectory);
                var factory = new com.infiniteconquest.cli.DemoMatchFactory();
                var build = new com.infiniteconquest.core.DeckBuild("Review", "ZEUS", "POSEIDON", factory.capitals().forFaction("ZEUS").get(0), new com.infiniteconquest.cli.FactionDecks(factory.pool()).starter("ZEUS"));
                for (int step=0;step<4;step++) new DeckBuilderDialog(gui,factory.pool(),factory.capitals(),build).captureForReview(step,outputDirectory.resolve("deck-builder-step-"+step+".png"));
            } finally { gui.dispose(); }
        });
        System.exit(0);
    }

    private static void capture(Path outputDirectory, String scenario, int width, int height) {
        InfiniteConquestGui gui = new InfiniteConquestGui(true);
        try {
            prepare(gui, scenario);
            gui.prepareCaptureSize(width, height, !scenario.equals("expanded-hand"));
            if(scenario.equals("opening-board"))gui.verifyHandOverlay();
            gui.captureScreenshot(outputDirectory.resolve(scenario + "-" + width + "x" + height + ".png"));
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
                    gui.prepareCaptureSize(1366, 768, false);
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
