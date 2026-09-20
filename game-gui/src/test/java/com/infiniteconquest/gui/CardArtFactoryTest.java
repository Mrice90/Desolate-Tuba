package com.infiniteconquest.gui;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardArtFactoryTest {
    private static final List<String> CAPITAL_IDS = List.of(
            "zeus_capital_olympus_citadel", "zeus_capital_keraunos_spire", "zeus_capital_cloud_throne",
            "poseidon_capital_atlantis_nexus", "poseidon_capital_trident_bastion", "poseidon_capital_abyssal_court",
            "hades_capital_house_of_hades", "hades_capital_styx_gate", "hades_capital_tartarus_vault",
            "ares_capital_red_citadel", "ares_capital_iron_war_camp", "ares_capital_spearpoint_keep",
            "athena_capital_acropolis_command", "athena_capital_aegis_archive", "athena_capital_owlwatch_fortress",
            "hephaestus_capital_great_forge", "hephaestus_capital_volcanic_foundry", "hephaestus_capital_bronze_heart");

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

    @Test void packagesAUniquePaintedIllustrationForEveryCapital() {
        for (String id : CAPITAL_IDS) {
            assertNotNull(CardArtFactory.class.getResource("/art/capitals/" + id + ".jpg"), id);
        }
        CardDefinition capital = new CardDefinition("zeus_capital_olympus_citadel", "Olympus Citadel",
                CardType.CAPITAL, "ZEUS", 0, 0, 0, 0, 0, 20);
        assertTrue(CardArtFactory.hasPaintedArt(capital));
        ImageIcon wide = CardArtFactory.iconFor(capital, 300, 120);
        ImageIcon compact = CardArtFactory.boardIconFor(capital);
        assertEquals(300, wide.getIconWidth());
        assertEquals(120, wide.getIconHeight());
        assertEquals(78, compact.getIconWidth());
        assertEquals(56, compact.getIconHeight());
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
