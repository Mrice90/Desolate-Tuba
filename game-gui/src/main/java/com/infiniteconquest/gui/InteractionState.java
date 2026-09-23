package com.infiniteconquest.gui;

import com.infiniteconquest.core.BoardPosition;

import java.util.Objects;

/** Owns transient user interaction independently from authoritative game state. */
final class InteractionState {
    enum Mode { IDLE, HAND_SELECTED, BOARD_SELECTED, DRAGGING }

    private Mode mode = Mode.IDLE;
    private Integer handIndex;
    private BoardPosition boardPosition;

    Mode mode() { return mode; }
    Integer handIndex() { return handIndex; }
    BoardPosition boardPosition() { return boardPosition; }
    boolean hasSelection() { return handIndex != null || boardPosition != null; }

    void toggleHand(int index) {
        if (mode != Mode.DRAGGING && Objects.equals(handIndex, index)) {
            clearSelection();
            return;
        }
        selectHand(index);
    }

    void selectHand(int index) {
        handIndex = index;
        boardPosition = null;
        mode = Mode.HAND_SELECTED;
    }

    void toggleBoard(BoardPosition position) {
        if (mode != Mode.DRAGGING && Objects.equals(boardPosition, position)) {
            clearSelection();
            return;
        }
        selectBoard(position);
    }

    void selectBoard(BoardPosition position) {
        handIndex = null;
        boardPosition = Objects.requireNonNull(position);
        mode = Mode.BOARD_SELECTED;
    }

    void beginDrag(Integer hand, BoardPosition board) {
        if ((hand == null) == (board == null)) {
            throw new IllegalArgumentException("A drag needs exactly one source");
        }
        handIndex = hand;
        boardPosition = board;
        mode = Mode.DRAGGING;
    }

    void finishDrag() {
        if (handIndex != null) mode = Mode.HAND_SELECTED;
        else if (boardPosition != null) mode = Mode.BOARD_SELECTED;
        else mode = Mode.IDLE;
    }

    void clearSelection() {
        handIndex = null;
        boardPosition = null;
        mode = Mode.IDLE;
    }
}
