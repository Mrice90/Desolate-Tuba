package com.infiniteconquest.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardAbilityRulesTest {
    @Test void enterDestroyedPassiveAndPaidAbilitiesResolveFromCardData() {
        GameState state = new GameState(91L);
        CardInstance home = add(state, 0, permanent("home", List.of()), new BoardPosition(0, 0));
        CardInstance enemyCapital = add(state, 1, new CardDefinition("enemy_capital", "Enemy Capital",
                CardType.CAPITAL, "TEST", 0, 0, 0, 0, 0, 20), new BoardPosition(0, 5));

        CardInstance arrival = add(state, 0, permanent("arrival", List.of(
                ability(AbilityTrigger.ENTERS_PLAY, AbilityEffectType.GAIN_GP, 2, 0))), new BoardPosition(1, 0));
        state.recordCardPlayed(arrival);
        assertEquals(2, state.player(0).currentGp());

        CardInstance deathrattle = add(state, 0, permanent("deathrattle", List.of(
                ability(AbilityTrigger.DESTROYED, AbilityEffectType.DAMAGE_ENEMY_CAPITAL, 2, 0))), new BoardPosition(2, 0));
        state.destroy(deathrattle);
        assertEquals(2, enemyCapital.damage());

        CardInstance tide = add(state, 0, permanent("tide", List.of(
                ability(AbilityTrigger.PASSIVE, AbilityEffectType.HEAL_SELF, 1, 0))), new BoardPosition(3, 1));
        tide.addDamage(2);
        new CardAbilityRules().resolve(state, tide, AbilityTrigger.PASSIVE);
        assertEquals(1, tide.damage());

        CardInstance repair = add(state, 0, permanent("repair", List.of(
                ability(AbilityTrigger.ACTIVATED, AbilityEffectType.HEAL_SELF, 3, 2))), new BoardPosition(3, 0));
        repair.addDamage(4);
        ActionResult activated = new GameEngine().apply(state,
                new GameAction.ActivateAbility(0, repair.instanceId()));
        assertTrue(activated.accepted());
        assertEquals(1, repair.damage());
        assertEquals(0, state.player(0).currentGp());
        assertFalse(new GameEngine().apply(state,
                new GameAction.ActivateAbility(0, repair.instanceId())).accepted());
        assertEquals(Zone.BATTLEFIELD, home.zone());
    }

    private CardAbility ability(AbilityTrigger trigger, AbilityEffectType effect, int amount, int cost) {
        return new CardAbility(trigger, effect, amount, cost);
    }

    private CardDefinition permanent(String id, List<CardAbility> abilities) {
        return new CardDefinition(id, id, CardType.LAND, "TEST", 1,
                0, 0, 0, 0, 8, Set.of(), List.of(), 1, DevelopmentPassive.NONE, abilities);
    }

    private CardInstance add(GameState state, int owner, CardDefinition definition, BoardPosition position) {
        CardInstance card = new CardInstance(UUID.randomUUID(), definition, owner, Zone.BATTLEFIELD);
        state.register(card);
        state.board().push(position, card.instanceId());
        return card;
    }
}
