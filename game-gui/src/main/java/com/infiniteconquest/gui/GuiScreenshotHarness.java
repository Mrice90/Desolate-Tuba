package com.infiniteconquest.gui;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

/** Deterministic visual fixture runner used by CI under Xvfb. */
public final class GuiScreenshotHarness {
    private static final String[] SCENARIOS = {"opening-board", "selected-hand", "expanded-hand"};

    private GuiScreenshotHarness() { }

    public static void main(String[] args) throws Exception {
        Path outputDirectory = Path.of(args.length == 0 ? "build/screenshots" : args[0]);
        for (String scenario : SCENARIOS) {
            SwingUtilities.invokeAndWait(() -> capture(outputDirectory, scenario));
        }
    }

    private static void capture(Path outputDirectory, String scenario) {
        InfiniteConquestGui gui = new InfiniteConquestGui(true);
        try {
            gui.setSize(1500, 980);
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
            gui.prepareScreenshotScenario(scenario);
            gui.captureScreenshot(outputDirectory.resolve(scenario + ".png"));
        } finally {
            gui.dispose();
        }
    }
}
