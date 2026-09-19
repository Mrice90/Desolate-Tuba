package com.infiniteconquest.core;

import java.util.UUID;

public sealed interface GameAction permits GameAction.PlayLand, GameAction.EndTurn {
    int playerId();

    record PlayLand(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record EndTurn(int playerId) implements GameAction {}
}
