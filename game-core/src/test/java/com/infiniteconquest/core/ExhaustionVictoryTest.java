package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExhaustionVictoryTest {
    @Test
    void failedDrawDestroysLethallyDamagedLastPermanent() {
        GameState state = new GameState(88L);
        CardDefinition capitalDefinition = new CardDefinition("test_capital", "Test Capital",
                CardType.CAPITAL, "TEST", 0, 0, 0, 0, 0, 1);
        CardInstance capital = new CardInstance(UUID.randomUUID(), capitalDefinition, 0, Zone.BATTLEFIELD);
        state.register(capital);
        state.board().push(new BoardPosition(1, 0), capital.instanceId());

        new GameEngine().apply(state, new GameAction.EndTurn(0));
        new GameEngine().apply(state, new GameAction.EndTurn(1));

        assertEquals(Phase.GAME_OVER, state.phase());
        assertEquals(1, state.winner().orElseThrow());
        assertEquals(Zone.DISCARD, capital.zone());
    }
}
