package com.infiniteconquest.core;

import com.infiniteconquest.data.Keyword;

import java.util.*;

public final class CapitalPassiveRules {
    private static final Map<String, CapitalPassive> BY_CAPITAL = Map.ofEntries(
            Map.entry("zeus_capital_olympus_citadel", CapitalPassive.OLYMPIAN_MUSTER),
            Map.entry("zeus_capital_keraunos_spire", CapitalPassive.STORM_TITHE),
            Map.entry("zeus_capital_cloud_throne", CapitalPassive.CLOUDWARD),
            Map.entry("poseidon_capital_atlantis_nexus", CapitalPassive.TIDAL_RENEWAL),
            Map.entry("poseidon_capital_trident_bastion", CapitalPassive.TRIDENT_RESTORATION),
            Map.entry("poseidon_capital_abyssal_court", CapitalPassive.DEEP_RESERVES),
            Map.entry("hades_capital_house_of_hades", CapitalPassive.DEATHLESS_LEVY),
            Map.entry("hades_capital_styx_gate", CapitalPassive.FERRY_TOLL),
            Map.entry("hades_capital_tartarus_vault", CapitalPassive.TARTARUS_ENDURANCE),
            Map.entry("ares_capital_red_citadel", CapitalPassive.BLOODLUST),
            Map.entry("ares_capital_iron_war_camp", CapitalPassive.WAR_CAMP_DRILL),
            Map.entry("ares_capital_spearpoint_keep", CapitalPassive.RELENTLESS_ADVANCE),
            Map.entry("athena_capital_acropolis_command", CapitalPassive.AEGIS_FORMATION),
            Map.entry("athena_capital_aegis_archive", CapitalPassive.ARCHIVED_FORESIGHT),
            Map.entry("athena_capital_owlwatch_fortress", CapitalPassive.OWLWARD),
            Map.entry("hephaestus_capital_great_forge", CapitalPassive.FORGE_EFFICIENCY),
            Map.entry("hephaestus_capital_volcanic_foundry", CapitalPassive.SALVAGE_FIRES),
            Map.entry("hephaestus_capital_bronze_heart", CapitalPassive.BRONZE_REGENERATION));

    private static final Map<CapitalPassive, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry(CapitalPassive.OLYMPIAN_MUSTER, "Start of your turn: your first Blink Character gains +3 Attack this turn."),
            Map.entry(CapitalPassive.STORM_TITHE, "The first Spell you cast each turn refunds 3 GP."),
            Map.entry(CapitalPassive.CLOUDWARD, "The first Character you Blink each turn gains +4 Defense until your next turn."),
            Map.entry(CapitalPassive.TIDAL_RENEWAL, "Start of your turn: heal 3 damage from your most damaged Land."),
            Map.entry(CapitalPassive.TRIDENT_RESTORATION, "The first Land you play each turn heals your Capital for 2."),
            Map.entry(CapitalPassive.DEEP_RESERVES, "The first Mole you burrow each turn refunds 2 GP."),
            Map.entry(CapitalPassive.DEATHLESS_LEVY, "Start of your turn: return your most recently discarded Character to your hand and heal your Capital for 3."),
            Map.entry(CapitalPassive.FERRY_TOLL, "Your first Spell each turn refunds 2 GP; returning an enemy Character restores 2 more."),
            Map.entry(CapitalPassive.TARTARUS_ENDURANCE, "The first friendly Permanent destroyed each turn heals another damaged friendly Permanent for 5."),
            Map.entry(CapitalPassive.BLOODLUST, "Your first attack each turn gains +1 Attack for that turn."),
            Map.entry(CapitalPassive.WAR_CAMP_DRILL, "The first Character you summon each turn gains +1 Attack until your next turn."),
            Map.entry(CapitalPassive.RELENTLESS_ADVANCE, "The first Character you move each turn recovers 1 movement."),
            Map.entry(CapitalPassive.AEGIS_FORMATION, "Start of your turn: your first Vanguard Character gains +1 Defense this turn."),
            Map.entry(CapitalPassive.ARCHIVED_FORESIGHT, "Draw one additional card every fourth personal turn."),
            Map.entry(CapitalPassive.OWLWARD, "The first enemy attack on your Vanguard Character each turn grants +1 Defense until your next turn."),
            Map.entry(CapitalPassive.FORGE_EFFICIENCY, "The first Structure you play each turn generates 1 GP."),
            Map.entry(CapitalPassive.SALVAGE_FIRES, "The first friendly Structure destroyed each turn heals your Capital for 2."),
            Map.entry(CapitalPassive.BRONZE_REGENERATION, "Start of your turn: heal your Capital for 1."));

    public Optional<CapitalPassive> passiveFor(CardDefinition capital) {
        if (capital.type() != CardType.CAPITAL) return Optional.empty();
        return Optional.ofNullable(BY_CAPITAL.get(capital.id()));
    }

    public String description(CardDefinition capital) {
        return passiveFor(capital).map(DESCRIPTIONS::get).orElse("No passive ability.");
    }

    public int supportedCapitalCount() { return BY_CAPITAL.size(); }

    void onTurnStarted(GameState state, int playerId) {
        CapitalPassive activePassive = passive(state, playerId).orElse(null);
        if (activePassive == null) return;
        switch (activePassive) {
            case OLYMPIAN_MUSTER -> firstBattlefieldCard(state, playerId,
                    card -> card.definition().type() == CardType.CHARACTER && card.definition().hasKeyword(Keyword.BLINK))
                    .ifPresent(card -> { card.addAttackBonus(3); trigger(state, playerId, CapitalPassive.OLYMPIAN_MUSTER); });
            case TIDAL_RENEWAL -> mostDamaged(state, playerId, CardType.LAND).ifPresent(card -> {
                card.healDamage(3); trigger(state, playerId, CapitalPassive.TIDAL_RENEWAL);
            });
            case DEATHLESS_LEVY -> state.returnMostRecentDiscardedCharacter(playerId).ifPresent(card -> {
                capital(state, playerId).ifPresent(value -> value.healDamage(3));
                trigger(state, playerId, CapitalPassive.DEATHLESS_LEVY);
            });
            case AEGIS_FORMATION -> firstBattlefieldCard(state, playerId,
                    card -> card.definition().type() == CardType.CHARACTER && card.definition().hasKeyword(Keyword.VANGUARD))
                    .ifPresent(card -> { card.addDefenseBonus(1); trigger(state, playerId, CapitalPassive.AEGIS_FORMATION); });
            case ARCHIVED_FORESIGHT -> {
                if (state.personalTurnNumber(playerId) % 4 == 0) {
                    state.drawCards(playerId, 1); trigger(state, playerId, CapitalPassive.ARCHIVED_FORESIGHT);
                }
            }
            case BRONZE_REGENERATION -> capital(state, playerId).filter(card -> card.damage() > 0).ifPresent(card -> {
                card.healDamage(1); trigger(state, playerId, CapitalPassive.BRONZE_REGENERATION);
            });
            default -> { }
        }
    }

    void onCardPlayed(GameState state, CardInstance card) {
        CapitalPassive passive = passive(state, card.owner()).orElse(null);
        if (passive == null) return;
        if (passive == CapitalPassive.STORM_TITHE && card.definition().type() == CardType.SPELL) refund(state, card.owner(), passive);
        if (passive == CapitalPassive.FERRY_TOLL && card.definition().type() == CardType.SPELL) refund(state, card.owner(), passive, 2);
        if (passive == CapitalPassive.TRIDENT_RESTORATION && card.definition().type() == CardType.LAND
                && use(state, card.owner(), passive)) {
            capital(state, card.owner()).ifPresent(value -> value.healDamage(2)); emit(state, card.owner(), passive);
        }
        if (passive == CapitalPassive.WAR_CAMP_DRILL && card.definition().type() == CardType.CHARACTER
                && use(state, card.owner(), passive)) { card.addAttackBonus(1); emit(state, card.owner(), passive); }
        if (passive == CapitalPassive.FORGE_EFFICIENCY && card.definition().type() == CardType.STRUCTURE) refund(state, card.owner(), passive);
    }

    void onBurrowed(GameState state, CardInstance card) {
        if (passive(state, card.owner()).orElse(null) == CapitalPassive.DEEP_RESERVES) refund(state, card.owner(), CapitalPassive.DEEP_RESERVES, 2);
    }

    void onBlinked(GameState state, CardInstance card) {
        if (passive(state, card.owner()).orElse(null) == CapitalPassive.CLOUDWARD
                && use(state, card.owner(), CapitalPassive.CLOUDWARD)) {
            card.addDefenseBonus(4); emit(state, card.owner(), CapitalPassive.CLOUDWARD);
        }
    }

    void onMoved(GameState state, CardInstance card) {
        if (passive(state, card.owner()).orElse(null) == CapitalPassive.RELENTLESS_ADVANCE
                && use(state, card.owner(), CapitalPassive.RELENTLESS_ADVANCE)) {
            card.restoreMovement(1); emit(state, card.owner(), CapitalPassive.RELENTLESS_ADVANCE);
        }
    }

    void beforeAttack(GameState state, CardInstance attacker, CardInstance target) {
        if (passive(state, attacker.owner()).orElse(null) == CapitalPassive.BLOODLUST
                && use(state, attacker.owner(), CapitalPassive.BLOODLUST)) {
            attacker.addAttackBonus(1); emit(state, attacker.owner(), CapitalPassive.BLOODLUST);
        }
        if (target.definition().type() == CardType.CHARACTER && target.definition().hasKeyword(Keyword.VANGUARD)
                && passive(state, target.owner()).orElse(null) == CapitalPassive.OWLWARD
                && use(state, target.owner(), CapitalPassive.OWLWARD)) {
            target.addDefenseBonus(1); emit(state, target.owner(), CapitalPassive.OWLWARD);
        }
    }

    void onCharacterReturnedBySpell(GameState state, int casterId, CardInstance target) {
        if (target.owner() != casterId && passive(state, casterId).orElse(null) == CapitalPassive.FERRY_TOLL) {
            state.player(casterId).restoreGp(2);
            emit(state, casterId, CapitalPassive.FERRY_TOLL);
        }
    }

    void onPermanentDestroyed(GameState state, CardInstance destroyed) {
        int owner = destroyed.owner();
        CapitalPassive passive = passive(state, owner).orElse(null);
        if (passive == CapitalPassive.TARTARUS_ENDURANCE && use(state, owner, passive)) {
            mostDamagedPermanent(state, owner).ifPresent(card -> card.healDamage(5)); emit(state, owner, passive);
        }
        if (passive == CapitalPassive.SALVAGE_FIRES && destroyed.definition().type() == CardType.STRUCTURE
                && use(state, owner, passive)) {
            capital(state, owner).ifPresent(card -> card.healDamage(2)); emit(state, owner, passive);
        }
    }

    private Optional<CapitalPassive> passive(GameState state, int playerId) {
        return capital(state, playerId).flatMap(card -> passiveFor(card.definition()));
    }

    private Optional<CardInstance> capital(GameState state, int playerId) {
        return firstBattlefieldCard(state, playerId, card -> card.definition().type() == CardType.CAPITAL);
    }

    private Optional<CardInstance> firstBattlefieldCard(GameState state, int playerId,
                                                        java.util.function.Predicate<CardInstance> predicate) {
        return state.battlefieldCards(playerId).stream().filter(predicate)
                .min(Comparator.comparing(card -> card.instanceId().toString()));
    }

    private Optional<CardInstance> mostDamaged(GameState state, int playerId, CardType type) {
        return state.battlefieldCards(playerId).stream().filter(card -> card.definition().type() == type && card.damage() > 0)
                .max(Comparator.comparingInt(CardInstance::damage).thenComparing(card -> card.instanceId().toString()));
    }

    private Optional<CardInstance> mostDamagedPermanent(GameState state, int playerId) {
        return state.battlefieldCards(playerId).stream().filter(card -> card.definition().isPermanent() && card.damage() > 0)
                .max(Comparator.comparingInt(CardInstance::damage).thenComparing(card -> card.instanceId().toString()));
    }

    private void refund(GameState state, int playerId, CapitalPassive passive) {
        refund(state, playerId, passive, passive == CapitalPassive.STORM_TITHE ? 3 : 1);
    }

    private void refund(GameState state, int playerId, CapitalPassive passive, int amount) {
        if (use(state, playerId, passive)) {
            state.player(playerId).restoreGp(amount); emit(state, playerId, passive);
        }
    }

    private boolean use(GameState state, int playerId, CapitalPassive passive) {
        return state.tryUseCapitalPassive(playerId, passive);
    }

    private void trigger(GameState state, int playerId, CapitalPassive passive) {
        state.markCapitalPassiveUsed(playerId, passive); emit(state, playerId, passive);
    }

    private void emit(GameState state, int playerId, CapitalPassive passive) {
        state.recordCapitalPassive(playerId, passive, DESCRIPTIONS.get(passive));
    }
}
