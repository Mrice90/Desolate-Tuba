package com.infiniteconquest.core;

import java.util.Objects;

public final class GameEngine {
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
        return ActionResult.rejected("Unsupported action");
    }

    private ActionResult playLand(GameState state, GameAction.PlayLand action) {
        CardInstance card = state.card(action.cardId()).orElse(null);
        if (card == null) return ActionResult.rejected("Unknown card instance");
        if (card.owner() != action.playerId()) return ActionResult.rejected("Card is owned by another player");
        if (card.definition().type() != CardType.LAND) return ActionResult.rejected("Only a Land may use PlayLand");
        PlayerState player = state.player(action.playerId());
        if (!player.hasInHand(card.instanceId()) || card.zone() != Zone.HAND) {
            return ActionResult.rejected("Land must be in the active player's hand");
        }
        if (!state.board().isEmpty(action.destination())) {
            return ActionResult.rejected("Initial Land placement requires an empty cell");
        }
        if (card.definition().cost() > player.currentGp()) return ActionResult.rejected("Insufficient GP");

        player.spendGp(card.definition().cost());
        player.removeFromHand(card.instanceId());
        card.moveTo(Zone.BATTLEFIELD);
        state.board().push(action.destination(), card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Land played");
    }
}
