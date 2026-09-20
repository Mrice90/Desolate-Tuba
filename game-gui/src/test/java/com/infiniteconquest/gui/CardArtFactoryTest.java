package com.infiniteconquest.gui;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class CardArtFactoryTest {
    @Test void packagesFactionWorldsAndRendersDistinctCardIllustrations() {
        assertNotNull(CardArtFactory.class.getResource("/art/faction-environments.png"));
        assertTrue(VisualEffects.available(), "CC0 particle textures should be packaged");
        CardDefinition storm = new CardDefinition("test_storm_seer", "Storm Seer",
                CardType.CHARACTER, "ZEUS", 3, 3, 3, 2, 2);
        CardDefinition reef = new CardDefinition("test_reef_bastion", "Reef Bastion",
                CardType.STRUCTURE, "POSEIDON", 2, 0, 0, 0, 0, 8);

        ImageIcon first = CardArtFactory.iconFor(storm, 190, 78);
        ImageIcon second = CardArtFactory.iconFor(reef, 190, 78);
        assertEquals(190, first.getIconWidth());
        assertEquals(78, first.getIconHeight());
        assertNotEquals(pixelHash(first), pixelHash(second));
        assertSame(first, CardArtFactory.iconFor(storm, 190, 78), "rendered art should be cached");
        ImageIcon board = CardArtFactory.boardIconFor(storm);
        assertEquals(78, board.getIconWidth());
        assertEquals(56, board.getIconHeight());
    }

    private int pixelHash(ImageIcon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(icon.getImage(), 0, 0, null);
        int hash = 1;
        for (int y = 0; y < image.getHeight(); y += 5) {
            for (int x = 0; x < image.getWidth(); x += 5) hash = 31 * hash + image.getRGB(x, y);
        }
        return hash;
    }
}
