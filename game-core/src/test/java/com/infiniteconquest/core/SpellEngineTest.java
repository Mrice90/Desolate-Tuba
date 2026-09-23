package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SpellEngineTest {
    private CardInstance add(GameState state, int owner, CardDefinition definition,
                             Zone zone, BoardPosition position) {
        CardInstance card = new CardInstance(UUID.randomUUID(), definition, owner, zone);
        state.register(card);
        if (zone == Zone.HAND) state.player(owner).addToHand(card.instanceId());
        if (position != null) state.board().push(position, card.instanceId());
        return card;
    }

    private CardDefinition character(String id, int attack, int defense) {
        return new CardDefinition(id, id, CardType.CHARACTER, "TEST", 0,
                attack, defense, 2, 2, 0);
    }

    private CardDefinition spell(String id, SpellEffect effect) {
        return new CardDefinition(id, id, CardType.SPELL, "TEST", 0,
                0, 0, 0, 0, 0, Set.of(), List.of(effect));
    }

    @Test
    void inactivePlayerCanSpendSavedGpOnImmediateReactionBuff() {
        GameState state = new GameState(1L);
        GameEngine engine = new GameEngine();
        state.player(1).restoreGp(2);
        engine.apply(state, new GameAction.EndTurn(0));
        engine.apply(state, new GameAction.EndTurn(1));
        CardInstance defender = add(state, 1, character("defender", 2, 2),
                Zone.BATTLEFIELD, new BoardPosition(0, 2));
        CardInstance reaction = add(state, 1,
                new CardDefinition("reaction", "Reaction", CardType.SPELL, "TEST", 2,
                        0, 0, 0, 0, 0, Set.of(),
                        List.of(new SpellEffect(SpellEffectType.BUFF_DEFENSE, 3, SpellTarget.FRIENDLY))),
                Zone.HAND, null);

        ActionResult result = engine.apply(state,
                new GameAction.CastSpell(1, reaction.instanceId(), defender.instanceId(), null));

        assertTrue(result.accepted());
        assertEquals(5, defender.effectiveDefense());
        assertEquals(Zone.DISCARD, reaction.zone());
        assertEquals(1, state.player(1).currentGp());
        assertTrue(state.events().stream().anyMatch(event -> event.type() == GameEvent.Type.GP_SPENT
                && event.playerId() == 1 && event.detail().equals("2 for Reaction")));

        engine.apply(state, new GameAction.EndTurn(0));
        assertEquals(2, defender.effectiveDefense(), "buff expires at start of controller's next turn");
    }

    @Test
    void removalHealingTeleportAndReturnEffectsResolve() {
        GameState state = new GameState(2L);
        GameEngine engine = new GameEngine();
        add(state, 0, new CardDefinition("home", "Home", CardType.LAND, "TEST", 0, 0, 0, 0, 0, 8),
                Zone.BATTLEFIELD, new BoardPosition(3, 0));
        CardInstance enemyLand = add(state, 1,
                new CardDefinition("enemy_land", "Enemy", CardType.LAND, "TEST", 0, 0, 0, 0, 0, 8),
                Zone.BATTLEFIELD, new BoardPosition(3, 5));
        CardInstance enemy = add(state, 1, character("enemy", 2, 3),
                Zone.BATTLEFIELD, new BoardPosition(2, 4));
        CardInstance friendly = add(state, 0, character("friendly", 2, 2),
                Zone.BATTLEFIELD, new BoardPosition(0, 0));

        CardInstance strike = add(state, 0, spell("strike",
                new SpellEffect(SpellEffectType.STRIKE_CHARACTER, 4, SpellTarget.ENEMY)), Zone.HAND, null);
        assertTrue(engine.apply(state,
                new GameAction.CastSpell(0, strike.instanceId(), enemy.instanceId(), null)).accepted());
        assertEquals(Zone.DISCARD, enemy.zone());

        enemyLand.addDamage(6);
        CardInstance heal = add(state, 1, spell("heal",
                new SpellEffect(SpellEffectType.HEAL_PERMANENT, 4, SpellTarget.FRIENDLY)), Zone.HAND, null);
        assertTrue(engine.apply(state,
                new GameAction.CastSpell(1, heal.instanceId(), enemyLand.instanceId(), null)).accepted());
        assertEquals(2, enemyLand.damage());

        CardInstance teleport = add(state, 0, spell("teleport",
                new SpellEffect(SpellEffectType.TELEPORT_CHARACTER, 1, SpellTarget.FRIENDLY)), Zone.HAND, null);
        BoardPosition destination = new BoardPosition(2, 2);
        assertTrue(engine.apply(state,
                new GameAction.CastSpell(0, teleport.instanceId(), friendly.instanceId(), destination)).accepted());
        assertEquals(destination, state.board().positionOf(friendly.instanceId()).orElseThrow());

        CardInstance recall = add(state, 0, spell("recall",
                new SpellEffect(SpellEffectType.RETURN_CHARACTER, 1, SpellTarget.FRIENDLY)), Zone.HAND, null);
        assertTrue(engine.apply(state,
                new GameAction.CastSpell(0, recall.instanceId(), friendly.instanceId(), null)).accepted());
        assertEquals(Zone.HAND, friendly.zone());
        assertTrue(state.player(0).hasInHand(friendly.instanceId()));
    }

    @Test
    void permanentDamageAndAttackBuffUseTypedValues() {
        GameState state = new GameState(3L);
        GameEngine engine = new GameEngine();
        CardInstance target = add(state, 1,
                new CardDefinition("target_land", "Target", CardType.LAND, "TEST", 0, 0, 0, 0, 0, 7),
                Zone.BATTLEFIELD, new BoardPosition(0, 4));
        CardInstance attacker = add(state, 0, character("attacker", 2, 2),
                Zone.BATTLEFIELD, new BoardPosition(0, 0));
        CardInstance damage = add(state, 0, spell("damage",
                new SpellEffect(SpellEffectType.DAMAGE_PERMANENT, 5, SpellTarget.ENEMY)), Zone.HAND, null);
        CardInstance buff = add(state, 0, spell("buff",
                new SpellEffect(SpellEffectType.BUFF_ATTACK, 3, SpellTarget.FRIENDLY)), Zone.HAND, null);

        assertTrue(engine.apply(state,
                new GameAction.CastSpell(0, damage.instanceId(), target.instanceId(), null)).accepted());
        assertEquals(5, target.damage());
        assertTrue(engine.apply(state,
                new GameAction.CastSpell(0, buff.instanceId(), attacker.instanceId(), null)).accepted());
        assertEquals(5, attacker.effectiveAttack());
    }

    @Test
    void invalidAllegianceOrCoveredTargetDoesNotSpendSpell() {
        GameState state = new GameState(4L);
        GameEngine engine = new GameEngine();
        BoardPosition stack = new BoardPosition(0, 0);
        CardInstance buried = add(state, 0, character("buried", 2, 2), Zone.BATTLEFIELD, stack);
        add(state, 0, character("cover", 2, 2), Zone.BATTLEFIELD, stack);
        CardInstance hostileSpell = add(state, 0, spell("hostile",
                new SpellEffect(SpellEffectType.STRIKE_CHARACTER, 5, SpellTarget.ENEMY)), Zone.HAND, null);

        assertFalse(engine.apply(state,
                new GameAction.CastSpell(0, hostileSpell.instanceId(), buried.instanceId(), null)).accepted());
        assertEquals(Zone.HAND, hostileSpell.zone());
    }
}
