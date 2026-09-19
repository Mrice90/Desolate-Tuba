package com.infiniteconquest.core;

public record GameEvent(long sequence, int turnNumber, int playerId, Type type, String detail) {
    public enum Type {
        MATCH_STARTED, PHASE_CHANGED, TURN_STARTED, CARD_DRAWN, DRAW_FAILED,
        EXHAUSTION_DAMAGE, CARDS_UNTAPPED, CARD_PLAYED, CHARACTER_MOVED,
        ATTACK_RESOLVED, CARD_DESTROYED, CAPITALS_REVEALED, CAPITAL_PASSIVE_TRIGGERED,
        GAME_OVER, TURN_ENDED
    }
}
