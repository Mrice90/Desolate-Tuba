package com.infiniteconquest.core;

import java.util.UUID;

public sealed interface GameAction permits GameAction.PlayLand, GameAction.PlayStructure,
        GameAction.SummonCharacter, GameAction.BurrowCharacter, GameAction.MoveCharacter,
        GameAction.BlinkCharacter, GameAction.Attack, GameAction.EndTurn {
    int playerId();
    record PlayLand(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record PlayStructure(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record SummonCharacter(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record BurrowCharacter(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record MoveCharacter(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record BlinkCharacter(int playerId, UUID cardId, BoardPosition destination) implements GameAction {}
    record Attack(int playerId, UUID attackerId, UUID targetId) implements GameAction {}
    record EndTurn(int playerId) implements GameAction {}
}
