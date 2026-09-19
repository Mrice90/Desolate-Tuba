package com.infiniteconquest.core;

import java.util.*;

public final class GameEngine {
    private final MovementRules movementRules = new MovementRules();

    public ActionResult apply(GameState state, GameAction action) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(action);
        if (state.phase() != Phase.PLAY) return ActionResult.rejected("Actions require the Play phase");
        if (action.playerId() != state.activePlayer()) return ActionResult.rejected("Not the active player");
        if (action instanceof GameAction.EndTurn) {
            state.advanceTurn();
            return ActionResult.accepted("Turn ended");
        }
        if (action instanceof GameAction.PlayLand play) return playLand(state, play);
        if (action instanceof GameAction.MoveCharacter move) return moveCharacter(state, move);
        return ActionResult.rejected("Unsupported action");
    }

    public Set<BoardPosition> legalMovementDestinations(GameState state, UUID cardId) {
        return state.card(cardId).map(card -> movementRules.legalDestinations(state, card)).orElse(Set.of());
    }

    private ActionResult moveCharacter(GameState state, GameAction.MoveCharacter action) {
        CardInstance card = state.card(action.cardId()).orElse(null);
        if (card == null) return ActionResult.rejected("Unknown card instance");
        if (card.owner() != action.playerId()) return ActionResult.rejected("Character is owned by another player");
        if (card.definition().type() != CardType.CHARACTER) return ActionResult.rejected("Only Characters can move");
        int distance = movementRules.shortestLegalDistance(state, card, action.destination());
        if (distance < 0) return ActionResult.rejected("Destination is not reachable with remaining movement");
        BoardPosition origin = state.board().positionOf(card.instanceId()).orElseThrow();
        state.board().moveTop(origin, action.destination(), card.instanceId());
        card.spendMovement(distance);
        state.recordCharacterMoved(card, origin, action.destination(), distance);
        return ActionResult.accepted("Character moved");
    }

    private ActionResult playLand(GameState state, GameAction.PlayLand action) {
        CardInstance card = state.card(action.cardId()).orElse(null);
        if (card == null) return ActionResult.rejected("Unknown card instance");
        if (card.owner() != action.playerId()) return ActionResult.rejected("Card is owned by another player");
        if (card.definition().type() != CardType.LAND) return ActionResult.rejected("Only a Land may use PlayLand");
        PlayerState player = state.player(action.playerId());
        if (!player.hasInHand(card.instanceId()) || card.zone() != Zone.HAND) return ActionResult.rejected("Land must be in hand");
        if (!action.destination().isOnPlayerSide(action.playerId())) return ActionResult.rejected("Land must be played on its owner's plot");
        if (!state.board().isEmpty(action.destination())) return ActionResult.rejected("Land placement requires an empty cell");
        if (card.definition().cost() > player.currentGp()) return ActionResult.rejected("Insufficient GP");
        player.spendGp(card.definition().cost());
        player.removeFromHand(card.instanceId());
        card.moveTo(Zone.BATTLEFIELD);
        state.board().push(action.destination(), card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Land played");
    }
}
