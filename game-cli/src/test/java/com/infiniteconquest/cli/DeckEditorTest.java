package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckEditorTest {
    @TempDir Path temporaryDirectory;

    @Test
    void expandedPoolProvidesMeaningfulChoicesAcrossPlayableTypes() {
        PrototypeCardPool pool = new PrototypeCardPool();

        assertEquals(144, pool.cards().size());
        assertTrue(pool.cards().stream().anyMatch(card -> card.id().equals("demo_burrower_mole")));
        assertTrue(pool.cards().stream().anyMatch(card -> card.id().equals("demo_skybridge")));
        assertTrue(pool.cards().stream().anyMatch(card -> card.id().equals("demo_shield_generator")));
    }

    @Test
    void editorStartsFromValidDemoAndSwapsWithoutBreakingFortyCardRule() {
        DemoMatchFactory matches = new DemoMatchFactory();
        DeckEditor editor = new DeckEditor(matches.pool(), matches.demoDeck());

        assertTrue(editor.validationErrors().isEmpty());
        editor.swap("neo_proto_naiad_recon_droid", "demo_burrower_mole");

        assertEquals(40, editor.cards().size());
        assertEquals(3L, editor.counts().get("neo_proto_naiad_recon_droid"));
        assertEquals(1L, editor.counts().get("demo_burrower_mole"));
        assertTrue(editor.validationErrors().isEmpty());
    }

    @Test
    void copyLimitAndExactDeckSizeAreEnforced() {
        DemoMatchFactory matches = new DemoMatchFactory();
        DeckEditor editor = new DeckEditor(matches.pool(), matches.demoDeck());

        assertThrows(IllegalArgumentException.class,
                () -> editor.swap("demo_land_a", "neo_proto_talus_defender"));
        editor.remove("demo_land_a");
        assertTrue(editor.validationErrors().contains("Deck must contain exactly 40 cards"));
        assertThrows(IllegalArgumentException.class,
                () -> new DeckFileStore().save(temporaryDirectory.resolve("invalid.json"), "Invalid", editor.cards()));
    }

    @Test
    void validCustomDeckRoundTripsAndCanStartAMatch() {
        DemoMatchFactory matches = new DemoMatchFactory();
        DeckEditor editor = new DeckEditor(matches.pool(), matches.demoDeck());
        editor.swap("neo_proto_naiad_recon_droid", "demo_burrower_mole");
        editor.swap("demo_land_a", "demo_skybridge");
        Path file = temporaryDirectory.resolve("custom.json");
        DeckFileStore store = new DeckFileStore();

        store.save(file, "Custom", editor.cards());
        List<CardDefinition> loaded = store.load(file, matches.pool());

        assertEquals(40, loaded.size());
        assertTrue(loaded.stream().anyMatch(card -> card.id().equals("demo_burrower_mole")));
        assertNotNull(matches.create(99L, loaded, matches.demoDeck()));
    }
}
