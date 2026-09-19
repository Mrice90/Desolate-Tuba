package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;
import com.infiniteconquest.data.Keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ActionHints {
    public List<String> forActivePlayer(GameState state, GameEngine engine) {
        int player = state.activePlayer();
        List<String> hints = new ArrayList<>();
        List<UUID> hand = state.player(player).hand();

        for (int index = 0; index < hand.size(); index++) {
            CardInstance card = state.card(hand.get(index)).orElseThrow();
            if (card.definition().cost() > state.player(player).currentGp()) continue;
            for (BoardPosition position : state.board().positions()) {
                if (card.definition().type() == CardType.LAND
                        && position.isOnPlayerSide(player) && state.board().isEmpty(position)) {
                    hints.add("play " + index + " " + position.x() + " " + position.y());
                } else if (card.definition().type() == CardType.STRUCTURE && isControlledTopLand(state, player, position)) {
                    hints.add("play " + index + " " + position.x() + " " + position.y());
                } else if (card.definition().type() == CardType.CHARACTER && legalSummonCell(state, player, position)) {
                    hints.add("play " + index + " " + position.x() + " " + position.y());
                }
                if (card.definition().type() == CardType.CHARACTER
                        && card.definition().hasKeyword(Keyword.MOLE)
                        && isControlledTopLand(state, player, position)) {
                    hints.add("burrow " + index + " " + position.x() + " " + position.y());
                }
            }
        }

        for (BoardPosition from : state.board().positions()) {
            var top = state.board().topAt(from);
            if (top.isEmpty()) continue;
            CardInstance card = state.card(top.orElseThrow()).orElseThrow();
            if (card.owner() != player || card.definition().type() != CardType.CHARACTER) continue;
            for (BoardPosition to : engine.legalMovementDestinations(state, card.instanceId())) {
                hints.add("move " + from.x() + " " + from.y() + " " + to.x() + " " + to.y());
            }
            if (card.definition().hasKeyword(Keyword.BLINK) && !card.blinkUsedThisTurn()) {
                for (BoardPosition to : state.board().positions()) if (state.board().isEmpty(to)) {
                    hints.add("blink " + from.x() + " " + from.y() + " " + to.x() + " " + to.y());
                }
            }
            for (BoardPosition to : engine.legalAttackDestinations(state, card.instanceId())) {
                hints.add("attack " + from.x() + " " + from.y() + " " + to.x() + " " + to.y());
            }
        }
        hints.add("end");
        return List.copyOf(hints);
    }

    private boolean isControlledTopLand(GameState state, int player, BoardPosition position) {
        return state.board().topAt(position).flatMap(state::card)
                .filter(card -> card.owner() == player)
                .map(card -> card.definition().type() == CardType.LAND)
                .orElse(false);
    }

    private boolean legalSummonCell(GameState state, int player, BoardPosition destination) {
        boolean onPermanent = state.board().stackAt(destination).stream()
                .map(id -> state.card(id).orElseThrow())
                .anyMatch(card -> card.owner() == player && card.definition().isPermanent());
        boolean besidePermanent = state.board().isEmpty(destination) && state.board().positions().stream()
                .filter(destination::adjacentTo)
                .flatMap(position -> state.board().stackAt(position).stream())
                .map(id -> state.card(id).orElseThrow())
                .anyMatch(card -> card.owner() == player && card.definition().isPermanent());
        return onPermanent || besidePermanent;
    }
}
