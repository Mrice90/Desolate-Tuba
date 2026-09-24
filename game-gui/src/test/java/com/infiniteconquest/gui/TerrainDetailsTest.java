package com.infiniteconquest.gui;
import com.infiniteconquest.cli.PrototypeCardPool;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TerrainDetailsTest {
    @Test void editorShowsTurnGoldArchetypeAndExactFunctionalParameters() {
        var pool=new PrototypeCardPool();String turret=DeckBuilderDialog.details(pool.require("ares_ballistic_shrine"));
        assertTrue(turret.contains("Turn 2 · 2 Gold"));assertTrue(turret.contains("range 2"));assertTrue(turret.contains("takes 1"));assertTrue(turret.contains("EMPLACEMENT"));
        String basic=DeckBuilderDialog.details(pool.require("zeus_olympian_cloudbank"));assertTrue(basic.contains("free"));
        assertTrue(DeckBuilderDialog.details(pool.require("poseidon_structure_pearl_infirmary")).contains("heals 2"));
    }
}
