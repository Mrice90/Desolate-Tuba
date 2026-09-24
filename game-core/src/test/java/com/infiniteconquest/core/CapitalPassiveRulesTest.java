package com.infiniteconquest.core;

import com.infiniteconquest.data.CardCatalog;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CapitalPassiveRulesTest {
    private final List<CardDefinition> capitals = CardCatalog.loadResource("/cards/faction-capitals.json").definitions();

    @Test
    void allEighteenCapitalsHaveDifferentImplementedPassives() {
        CapitalPassiveRules rules = new CapitalPassiveRules();
        assertEquals(18, rules.supportedCapitalCount());
        Set<CapitalPassive> passives = new HashSet<>();
        Set<String> descriptions = new HashSet<>();
        for (CardDefinition capital : capitals) {
            passives.add(rules.passiveFor(capital).orElseThrow());
            descriptions.add(rules.description(capital));
        }
        assertEquals(18, passives.size());
        assertEquals(18, descriptions.size());
    }

    @Test
    void stormTitheRefundsOnlyTheFirstSpellEachTurn() {
        GameState state = new GameState(1L);
        state.player(0).restoreGp(1);
        add(state, 0, capital("zeus_capital_keraunos_spire"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        CardDefinition spellDefinition = new CardDefinition("test_spell", "Test Spell", CardType.SPELL, "ZEUS",
                1, 0, 0, 0, 0, 0, Set.of(), List.of(new SpellEffect(SpellEffectType.BUFF_ATTACK, 1, SpellTarget.FRIENDLY)));
        CardInstance spell = add(state, 0, spellDefinition, Zone.HAND, null);
        CardInstance target = add(state, 0, character("target", 1, 1, 1), Zone.BATTLEFIELD, new BoardPosition(0, 0));

        assertTrue(new GameEngine().apply(state,
                new GameAction.CastSpell(0, spell.instanceId(), target.instanceId(), null)).accepted());
        assertEquals(1, state.player(0).currentGp());
        assertEquals(1, passiveEvents(state, CapitalPassive.STORM_TITHE));
    }

    @Test
    void bloodlustTurnsAnEqualAttackIntoAWinningAttack() {
        GameState state = new GameState(2L);
        add(state, 0, capital("ares_capital_red_citadel"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        CardInstance attacker = add(state, 0, character("attacker", 2, 1, 1), Zone.BATTLEFIELD, new BoardPosition(0, 0));
        CardInstance defender = add(state, 1, character("defender", 1, 2, 1), Zone.BATTLEFIELD, new BoardPosition(0, 1));

        assertTrue(new GameEngine().apply(state,
                new GameAction.Attack(0, attacker.instanceId(), defender.instanceId())).accepted());
        assertEquals(Zone.DISCARD, defender.zone());
        assertEquals(1, passiveEvents(state, CapitalPassive.BLOODLUST));
    }

    @Test
    void relentlessAdvanceReturnsOneMovementAfterFirstMove() {
        GameState state = new GameState(3L);
        add(state, 0, capital("ares_capital_spearpoint_keep"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        CardInstance mover = add(state, 0, character("mover", 2, 2, 3), Zone.BATTLEFIELD, new BoardPosition(0, 0));

        assertTrue(new GameEngine().apply(state,
                new GameAction.MoveCharacter(0, mover.instanceId(), new BoardPosition(0, 2))).accepted());
        assertEquals(1, mover.movementSpent());
        assertEquals(1, passiveEvents(state, CapitalPassive.RELENTLESS_ADVANCE));
    }

    @Test
    void bronzeRegenerationHealsAtStartOfOwnersTurn() {
        GameState state = new GameState(4L);
        CardInstance bronzeHeart = add(state, 0, capital("hephaestus_capital_bronze_heart"),
                Zone.BATTLEFIELD, new BoardPosition(1, 0));
        bronzeHeart.addDamage(5);
        CardInstance nextDraw = add(state, 0, character("next_draw", 1, 1, 1), Zone.DECK, null);
        state.player(0).loadDeck(List.of(nextDraw.instanceId()));

        new GameEngine().apply(state, new GameAction.EndTurn(0));
        new GameEngine().apply(state, new GameAction.EndTurn(1));

        assertEquals(4, bronzeHeart.damage());
        assertEquals(1, passiveEvents(state, CapitalPassive.BRONZE_REGENERATION));
    }

    @Test
    void ferryTollRewardsOnlyTheFirstEnemyReturnAndResetsNextTurn() {
        GameState state = new GameState(8L);
        var rules = new CapitalPassiveRules();
        add(state, 0, capital("hades_capital_styx_gate"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        var enemy = add(state, 1, character("enemy", 2, 2, 2), Zone.HAND, null);
        var friendly = add(state, 0, character("friendly", 2, 2, 2), Zone.HAND, null);
        var spell = add(state, 0, new CardDefinition("spell", "Spell", CardType.SPELL, "HADES", 4,
                0, 0, 0, 0, 0, Set.of(), List.of(new SpellEffect(SpellEffectType.RETURN_CHARACTER, 1, SpellTarget.ENEMY))), Zone.HAND, null);
        int start = state.player(0).currentGp();
        rules.onCardPlayed(state, spell);
        rules.onCharacterReturnedBySpell(state, 0, friendly);
        assertEquals(start, state.player(0).currentGp());
        rules.onCharacterReturnedBySpell(state, 0, enemy);
        rules.onCharacterReturnedBySpell(state, 0, enemy);
        assertEquals(start + 2, state.player(0).currentGp());
        assertEquals(1, passiveEvents(state, CapitalPassive.FERRY_TOLL));
        new GameEngine().apply(state, new GameAction.EndTurn(0));
        int next = state.player(0).currentGp();
        rules.onCharacterReturnedBySpell(state, 0, enemy);
        assertEquals(next + 2, state.player(0).currentGp());
    }

    @Test
    void deathlessLevyReturnsOneCharacterWithoutAlsoRepairingCapital() {
        GameState state = new GameState(9L);
        var house = add(state, 0, capital("hades_capital_house_of_hades"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        house.addDamage(6);
        var first = add(state, 0, character("first", 1, 1, 1), Zone.DISCARD, null);
        var last = add(state, 0, character("last", 2, 1, 1), Zone.DISCARD, null);
        state.player(0).addToDiscard(first.instanceId()); state.player(0).addToDiscard(last.instanceId());
        new CapitalPassiveRules().onTurnStarted(state, 0);
        assertEquals(Zone.HAND, last.zone()); assertEquals(Zone.DISCARD, first.zone());
        assertEquals(6, house.damage());
    }

    @Test
    void zeusCapitalBonusesUseTheNewTwoPointBudget() {
        var rules = new CapitalPassiveRules();
        var blink = new CardDefinition("blink", "Blink", CardType.CHARACTER, "ZEUS", 3,
                2, 2, 3, 1, 0, Set.of(com.infiniteconquest.data.Keyword.BLINK));
        GameState muster = new GameState(10L);
        add(muster, 0, capital("zeus_capital_olympus_citadel"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        var attacker = add(muster, 0, blink, Zone.BATTLEFIELD, new BoardPosition(0, 0));
        rules.onTurnStarted(muster, 0); assertEquals(4, attacker.effectiveAttack());
        GameState cloud = new GameState(11L);
        add(cloud, 0, capital("zeus_capital_cloud_throne"), Zone.BATTLEFIELD, new BoardPosition(1, 0));
        var defender = add(cloud, 0, blink, Zone.BATTLEFIELD, new BoardPosition(0, 0));
        rules.onBlinked(cloud, defender); rules.onBlinked(cloud, defender);
        assertEquals(4, defender.effectiveDefense());
    }

    private CardDefinition capital(String id) {
        return capitals.stream().filter(card -> card.id().equals(id)).findFirst().orElseThrow();
    }

    private CardDefinition character(String id, int attack, int defense, int movement) {
        return new CardDefinition(id, id, CardType.CHARACTER, "TEST", 0, attack, defense, movement, 1);
    }

    private CardInstance add(GameState state, int owner, CardDefinition definition, Zone zone, BoardPosition position) {
        CardInstance card = new CardInstance(UUID.randomUUID(), definition, owner, zone);
        state.register(card);
        if (zone == Zone.HAND) state.player(owner).addToHand(card.instanceId());
        if (position != null) state.board().push(position, card.instanceId());
        return card;
    }

    private long passiveEvents(GameState state, CapitalPassive passive) {
        return state.events().stream().filter(event -> event.type() == GameEvent.Type.CAPITAL_PASSIVE_TRIGGERED)
                .filter(event -> event.detail().startsWith(passive.name())).count();
    }
}
