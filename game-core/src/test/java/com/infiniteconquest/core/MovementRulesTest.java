package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MovementRulesTest {
    private CardInstance character(GameState state, int movement, BoardPosition position) {
        CardDefinition definition = new CardDefinition("runner", "Runner", CardType.CHARACTER, "DEV", 0, 1, 1, movement, 1);
        CardInstance card = new CardInstance(UUID.randomUUID(), definition, 0, Zone.BATTLEFIELD);
        state.register(card);
        state.board().push(position, card.instanceId());
        return card;
    }

    @Test void diagonalMovementCostsOneAndCanBeSplitAcrossActions() {
        GameState state = new GameState(1L);
        CardInstance runner = character(state, 3, new BoardPosition(0, 0));
        GameEngine engine = new GameEngine();

        assertTrue(engine.legalMovementDestinations(state, runner.instanceId()).contains(new BoardPosition(1, 1)));
        assertTrue(engine.apply(state, new GameAction.MoveCharacter(0, runner.instanceId(), new BoardPosition(1, 1))).accepted());
        assertEquals(1, runner.movementSpent());

        assertTrue(engine.apply(state, new GameAction.MoveCharacter(0, runner.instanceId(), new BoardPosition(3, 3))).accepted());
        assertEquals(3, runner.movementSpent());
        assertTrue(engine.legalMovementDestinations(state, runner.instanceId()).isEmpty());
    }

    @Test void occupiedCellsBlockOrdinaryMovementPaths() {
        GameState state = new GameState(2L);
        CardInstance runner = character(state, 1, new BoardPosition(0, 0));
        CardDefinition landDef = new CardDefinition("block", "Block", CardType.LAND, "DEV", 0, 0, 0, 0, 0);
        CardInstance blocker = new CardInstance(UUID.randomUUID(), landDef, 0, Zone.BATTLEFIELD);
        state.register(blocker);
        state.board().push(new BoardPosition(1, 1), blocker.instanceId());

        assertFalse(new GameEngine().legalMovementDestinations(state, runner.instanceId()).contains(new BoardPosition(1, 1)));
    }
}
