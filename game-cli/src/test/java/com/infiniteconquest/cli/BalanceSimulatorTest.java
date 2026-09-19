package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BalanceSimulatorTest {
    @Test
    void headlessMatchLetsBotsControlBothPlayersDeterministically() {
        DemoMatchFactory factory = new DemoMatchFactory();
        BalanceSimulator simulator = new BalanceSimulator();
        CardDefinition zeusCapital = factory.capitals().require("zeus_capital_keraunos_spire");
        CardDefinition aresCapital = factory.capitals().require("ares_capital_red_citadel");

        BalanceSimulator.MatchResult first = simulator.play(42L, "ZEUS", "ARES", zeusCapital, aresCapital);
        BalanceSimulator.MatchResult second = simulator.play(42L, "ZEUS", "ARES", zeusCapital, aresCapital);

        assertEquals(first.winner(), second.winner());
        assertEquals(first.turns(), second.turns());
        assertEquals(first.cardPlays(), second.cardPlays());
        assertTrue(first.turns() <= BalanceSimulator.MAX_TURNS);
        assertFalse(first.cardPlays().isEmpty());
    }

    @Test
    void reportSchemaCarriesCoreBalanceSignals() {
        BalanceReport report = new BalanceReport(1, 7L, 9, 8, 1, 18.5, 0.5,
                1.2, 4.0, 2, java.util.List.of(), java.util.List.of(), java.util.List.of(),
                java.util.List.of("Draw rate above 10%"));

        assertEquals(9, report.totalMatches());
        assertEquals(2, report.matchesWithExhaustion());
        assertFalse(report.balanceFlags().isEmpty());
    }
}
