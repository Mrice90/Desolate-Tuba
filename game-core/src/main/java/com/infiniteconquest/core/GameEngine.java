package com.infiniteconquest.core;

import com.infiniteconquest.data.Keyword;

import java.util.*;

public final class GameEngine {
    private final MovementRules movementRules = new MovementRules();
    private final LineOfSightRules lineOfSightRules = new LineOfSightRules();

    public ActionResult apply(GameState state, GameAction action) {
        Objects.requireNonNull(state); Objects.requireNonNull(action);
        if (state.phase() != Phase.PLAY) return ActionResult.rejected("Actions require Play phase");
        if (action.playerId() != state.activePlayer()) return ActionResult.rejected("Not active player");
        if (action instanceof GameAction.EndTurn) { state.advanceTurn(); return ActionResult.accepted("Turn ended"); }
        if (action instanceof GameAction.PlayLand a) return playLand(state, a);
        if (action instanceof GameAction.PlayStructure a) return playStructure(state, a);
        if (action instanceof GameAction.SummonCharacter a) return summonCharacter(state, a);
        if (action instanceof GameAction.BurrowCharacter a) return burrowCharacter(state, a);
        if (action instanceof GameAction.MoveCharacter a) return moveCharacter(state, a);
        if (action instanceof GameAction.BlinkCharacter a) return blinkCharacter(state, a);
        if (action instanceof GameAction.Attack a) return attack(state, a);
        return ActionResult.rejected("Unsupported action");
    }

    public Set<BoardPosition> legalMovementDestinations(GameState state, UUID id) {
        return state.card(id).map(c -> movementRules.legalDestinations(state, c)).orElse(Set.of());
    }

    public Set<BoardPosition> legalAttackDestinations(GameState state, UUID attackerId) {
        CardInstance attacker = state.card(attackerId).orElse(null);
        if (attacker == null || attacker.owner() != state.activePlayer()
                || attacker.definition().type() != CardType.CHARACTER || attacker.attackedThisTurn()) {
            return Set.of();
        }
        BoardPosition from = state.board().positionOf(attacker.instanceId()).orElse(null);
        if (from == null || !state.board().topAt(from).orElseThrow().equals(attacker.instanceId())) {
            return Set.of();
        }
        Set<BoardPosition> legal = new LinkedHashSet<>();
        for (BoardPosition to : state.board().positions()) {
            Optional<UUID> targetId = state.board().topAt(to);
            if (targetId.isEmpty()) continue;
            CardInstance target = state.card(targetId.orElseThrow()).orElseThrow();
            if (target.owner() != attacker.owner()
                    && (target.definition().type() == CardType.CHARACTER || target.definition().isPermanent())
                    && from.distanceTo(to) <= attacker.definition().range()
                    && lineOfSightRules.hasLineOfSight(state, from, to)) {
                legal.add(to);
            }
        }
        return Collections.unmodifiableSet(legal);
    }

    private ActionResult summonCharacter(GameState state, GameAction.SummonCharacter action) {
        CardInstance card = playableFromHand(state, action.playerId(), action.cardId(), CardType.CHARACTER);
        if (card == null) return ActionResult.rejected("Character must be owned, affordable, and in hand");
        BoardPosition destination = action.destination();
        boolean onFriendlyPermanent = state.board().stackAt(destination).stream()
                .map(id -> state.card(id).orElseThrow())
                .anyMatch(c -> c.owner() == action.playerId() && c.definition().isPermanent());
        boolean adjacentToFriendlyPermanent = state.board().positions().stream()
                .filter(destination::adjacentTo)
                .flatMap(p -> state.board().stackAt(p).stream())
                .map(id -> state.card(id).orElseThrow())
                .anyMatch(c -> c.owner() == action.playerId() && c.definition().isPermanent());
        if (!onFriendlyPermanent && !(state.board().isEmpty(destination) && adjacentToFriendlyPermanent)) {
            return ActionResult.rejected("Character must be on or within one space of a friendly Permanent");
        }
        payAndRemoveFromHand(state, card);
        card.moveTo(Zone.BATTLEFIELD);
        state.board().push(destination, card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Character summoned");
    }

    private ActionResult burrowCharacter(GameState state, GameAction.BurrowCharacter action) {
        CardInstance card = playableFromHand(state, action.playerId(), action.cardId(), CardType.CHARACTER);
        if (card == null || !card.definition().hasKeyword(Keyword.MOLE)) {
            return ActionResult.rejected("Only an affordable Mole Character in hand can burrow");
        }
        Optional<UUID> top = state.board().topAt(action.destination());
        if (top.isEmpty()) return ActionResult.rejected("Mole requires a controlled Land");
        CardInstance land = state.card(top.orElseThrow()).orElseThrow();
        if (land.owner() != action.playerId() || land.definition().type() != CardType.LAND) {
            return ActionResult.rejected("Mole requires a controlled Land on top of the stack");
        }
        payAndRemoveFromHand(state, card);
        card.moveTo(Zone.BATTLEFIELD);
        state.board().insertBelowTop(action.destination(), card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Mole burrowed beneath Land");
    }

    private ActionResult playStructure(GameState state, GameAction.PlayStructure action) {
        CardInstance card = playableFromHand(state, action.playerId(), action.cardId(), CardType.STRUCTURE);
        if (card == null) return ActionResult.rejected("Structure must be owned, affordable, and in hand");
        Optional<UUID> top = state.board().topAt(action.destination());
        if (top.isEmpty()) return ActionResult.rejected("Structure requires a controlled Land");
        CardInstance foundation = state.card(top.get()).orElseThrow();
        if (foundation.owner() != action.playerId() || foundation.definition().type() != CardType.LAND) {
            return ActionResult.rejected("Structure requires a controlled Land on top of the stack");
        }
        payAndRemoveFromHand(state, card);
        card.moveTo(Zone.BATTLEFIELD);
        state.board().push(action.destination(), card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Structure played");
    }

    private ActionResult blinkCharacter(GameState state, GameAction.BlinkCharacter action) {
        CardInstance card = state.card(action.cardId()).orElse(null);
        if (card == null || card.owner() != action.playerId() || card.definition().type() != CardType.CHARACTER
                || !card.definition().hasKeyword(Keyword.BLINK)) {
            return ActionResult.rejected("Invalid Blink Character");
        }
        if (card.blinkUsedThisTurn()) return ActionResult.rejected("Blink already used this turn");
        BoardPosition origin = state.board().positionOf(card.instanceId()).orElse(null);
        if (origin == null || !state.board().topAt(origin).orElseThrow().equals(card.instanceId())) {
            return ActionResult.rejected("Only the top Character can Blink");
        }
        if (!state.board().isEmpty(action.destination())) {
            return ActionResult.rejected("Blink destination must be empty");
        }
        state.board().moveTop(origin, action.destination(), card.instanceId());
        card.markBlinkUsed();
        state.recordCharacterMoved(card, origin, action.destination(), 0);
        return ActionResult.accepted("Character Blinked");
    }

    private ActionResult attack(GameState state, GameAction.Attack action) {
        CardInstance attacker = state.card(action.attackerId()).orElse(null);
        CardInstance target = state.card(action.targetId()).orElse(null);
        if (attacker == null || target == null) return ActionResult.rejected("Unknown attacker or target");
        if (attacker.owner() != action.playerId() || target.owner() == action.playerId()) return ActionResult.rejected("Invalid ownership");
        if (attacker.definition().type() != CardType.CHARACTER || attacker.attackedThisTurn()) return ActionResult.rejected("Attacker cannot attack");
        BoardPosition from = state.board().positionOf(attacker.instanceId()).orElse(null);
        BoardPosition to = state.board().positionOf(target.instanceId()).orElse(null);
        if (from == null || to == null) return ActionResult.rejected("Attacker and target must be on battlefield");
        if (!state.board().topAt(from).orElseThrow().equals(attacker.instanceId())
                || !state.board().topAt(to).orElseThrow().equals(target.instanceId())) return ActionResult.rejected("Only top cards interact");
        if (from.distanceTo(to) > attacker.definition().range()) return ActionResult.rejected("Target out of range");
        if (!lineOfSightRules.hasLineOfSight(state, from, to)) return ActionResult.rejected("Line of sight blocked");

        attacker.markAttacked();
        state.recordAttack(attacker, target);
        if (target.definition().type() == CardType.CHARACTER) {
            if (attacker.definition().attack() > target.definition().defense()) state.destroy(target);
        } else if (target.definition().isPermanent()) {
            target.addDamage(attacker.definition().attack());
            if (target.damage() >= target.definition().hitPoints()) state.destroy(target);
        } else return ActionResult.rejected("Target cannot be attacked");
        return ActionResult.accepted("Attack resolved");
    }

    private ActionResult moveCharacter(GameState state, GameAction.MoveCharacter action) {
        CardInstance card = state.card(action.cardId()).orElse(null);
        if (card == null || card.owner() != action.playerId() || card.definition().type() != CardType.CHARACTER)
            return ActionResult.rejected("Invalid Character");
        int distance = movementRules.shortestLegalDistance(state, card, action.destination());
        if (distance < 0) return ActionResult.rejected("Destination unreachable");
        BoardPosition origin = state.board().positionOf(card.instanceId()).orElseThrow();
        state.board().moveTop(origin, action.destination(), card.instanceId());
        card.spendMovement(distance);
        state.recordCharacterMoved(card, origin, action.destination(), distance);
        return ActionResult.accepted("Character moved");
    }

    private ActionResult playLand(GameState state, GameAction.PlayLand action) {
        CardInstance card = playableFromHand(state, action.playerId(), action.cardId(), CardType.LAND);
        if (card == null) return ActionResult.rejected("Land must be owned, affordable, and in hand");
        if (!action.destination().isOnPlayerSide(action.playerId()) || !state.board().isEmpty(action.destination()))
            return ActionResult.rejected("Land requires an empty space on owner's plot");
        payAndRemoveFromHand(state, card);
        card.moveTo(Zone.BATTLEFIELD);
        state.board().push(action.destination(), card.instanceId());
        state.recordCardPlayed(card);
        return ActionResult.accepted("Land played");
    }

    private CardInstance playableFromHand(GameState state, int playerId, UUID id, CardType type) {
        CardInstance card = state.card(id).orElse(null);
        if (card == null || card.owner() != playerId || card.definition().type() != type
                || card.zone() != Zone.HAND || !state.player(playerId).hasInHand(id)
                || card.definition().cost() > state.player(playerId).currentGp()) return null;
        return card;
    }
    private void payAndRemoveFromHand(GameState state, CardInstance card) {
        state.player(card.owner()).spendGp(card.definition().cost());
        state.player(card.owner()).removeFromHand(card.instanceId());
    }
}
