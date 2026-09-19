package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConquestPressureTest {
    private CardInstance permanent(GameState state, int owner, String id, int hitPoints, BoardPosition position) {
        CardDefinition definition = new CardDefinition(id, id, CardType.CAPITAL, "TEST", 0,
                0, 0, 0, 0, hitPoints);
        CardInstance card = new CardInstance(UUID.randomUUID(), definition, owner, Zone.BATTLEFIELD);
        state.register(card);
        state.board().push(position, card.instanceId());
        return card;
    }

    @Test
    void conquestPressureBeginsOnTurnElevenAndEscalatesOnTurnSeventeen() {
        MatchRules rules = new MatchRules(0, 10, 2, 3, 1, 0);
        GameState state = new GameState(1L, rules, false);
        CardInstance first = permanent(state, 0, "first", 50, new BoardPosition(0, 0));
        CardInstance second = permanent(state, 1, "second", 50, new BoardPosition(0, 5));
        state.initializeMatch();

        while (state.turnNumber() < 11) state.advanceTurn();
        assertEquals(3, first.damage());
        assertEquals(0, second.damage());

        while (state.turnNumber() < 17) state.advanceTurn();
        assertEquals(13, first.damage());
        assertEquals(9, second.damage());
        assertTrue(state.events().stream().anyMatch(event -> event.type() == GameEvent.Type.CONQUEST_PRESSURE));
    }

    @Test
    void turnTwentyTwoDeadlineUsesPermanentCountThenHealth() {
        MatchRules rules = new MatchRules(0, 10, 2, 3, 1, 0);
        GameState state = new GameState(2L, rules, false);
        permanent(state, 0, "first", 100, new BoardPosition(0, 0));
        permanent(state, 0, "first_extra", 100, new BoardPosition(1, 0));
        permanent(state, 1, "second", 100, new BoardPosition(0, 5));
        state.initializeMatch();

        while (state.turnNumber() < 22) state.advanceTurn();
        state.advanceTurn();

        assertEquals(Phase.GAME_OVER, state.phase());
        assertEquals(0, state.winner().orElseThrow());
        assertTrue(state.events().get(state.events().size() - 1).detail().contains("deadline"));
    }
}
