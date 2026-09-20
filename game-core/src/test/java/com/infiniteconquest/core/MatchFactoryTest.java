package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MatchFactoryTest {
    private List<CardDefinition> validDeck(String prefix) {
        List<CardDefinition> cards = new ArrayList<>();
        for (int id = 0; id < 10; id++) {
            CardDefinition definition = new CardDefinition(
                    prefix + id, prefix + id, CardType.LAND, "DEV", 0, 0, 0, 0, 0);
            for (int copy = 0; copy < 4; copy++) cards.add(definition);
        }
        return cards;
    }

    @Test void sameSeedProducesSameOpeningState() {
        MatchFactory factory = new MatchFactory();
        GameState first = factory.create(849291L, MatchRules.current(), validDeck("a"), validDeck("b"));
        GameState second = factory.create(849291L, MatchRules.current(), validDeck("a"), validDeck("b"));

        assertEquals(first.player(0).hand(), second.player(0).hand());
        assertEquals(first.player(1).hand(), second.player(1).hand());
        assertEquals(first.startingPlayer(), second.startingPlayer());
        assertEquals(6, first.player(0).hand().size(), "Five-card provisional hand plus first Start Phase draw");
        assertEquals(34, first.player(0).deck().size());
        assertEquals(6, first.player(1).hand().size(), "Second player receives a sixth opening card");
        assertEquals(34, first.player(1).deck().size());
    }

    @Test void coinFlipVariesAndSecondPlayerGetsEconomyBonus() {
        MatchFactory factory = new MatchFactory();
        Set<Integer> winners = new HashSet<>();
        for (long seed = 1; seed <= 20; seed++) {
            GameState state = factory.create(seed, MatchRules.current(), validDeck("a"), validDeck("b"));
            winners.add(state.startingPlayer());
            assertEquals(10, state.player(state.startingPlayer()).currentGp());
            assertEquals(12, state.player(1 - state.startingPlayer()).currentGp());
        }
        assertEquals(Set.of(0, 1), winners);
    }

    @Test void differentSeedChangesOpeningOrder() {
        MatchFactory factory = new MatchFactory();
        GameState first = factory.create(1L, MatchRules.current(), validDeck("a"), validDeck("b"));
        GameState second = factory.create(2L, MatchRules.current(), validDeck("a"), validDeck("b"));
        assertNotEquals(first.player(0).hand(), second.player(0).hand());
    }

    @Test void rejectsInvalidDeckBeforeCreatingState() {
        assertThrows(IllegalArgumentException.class,
                () -> new MatchFactory().create(1L, MatchRules.current(), List.of(), validDeck("b")));
    }
}
