package com.infiniteconquest.gui;

import com.infiniteconquest.core.BoardPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionStateTest {
    @Test
    void handAndBoardSelectionsAreMutuallyExclusive() {
        InteractionState state = new InteractionState();
        state.selectHand(2);
        assertEquals(InteractionState.Mode.HAND_SELECTED, state.mode());
        assertEquals(2, state.handIndex());
        assertNull(state.boardPosition());

        BoardPosition cell = new BoardPosition(1, 1);
        state.selectBoard(cell);
        assertEquals(InteractionState.Mode.BOARD_SELECTED, state.mode());
        assertNull(state.handIndex());
        assertEquals(cell, state.boardPosition());
    }

    @Test
    void selectingTheSameSourceAgainReturnsToIdle() {
        InteractionState state = new InteractionState();
        state.toggleHand(1);
        state.toggleHand(1);
        assertEquals(InteractionState.Mode.IDLE, state.mode());
        assertFalse(state.hasSelection());
    }

    @Test
    void dragRetainsItsSourceUntilTheDropIsResolved() {
        InteractionState state = new InteractionState();
        state.beginDrag(3, null);
        assertEquals(InteractionState.Mode.DRAGGING, state.mode());
        assertEquals(3, state.handIndex());
        state.finishDrag();
        assertEquals(InteractionState.Mode.HAND_SELECTED, state.mode());
    }

    @Test
    void dragRequiresExactlyOneSource() {
        InteractionState state = new InteractionState();
        assertThrows(IllegalArgumentException.class, () -> state.beginDrag(null, null));
        assertThrows(IllegalArgumentException.class,
                () -> state.beginDrag(0, new BoardPosition(0, 0)));
    }
}
