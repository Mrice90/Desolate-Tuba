package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;
import com.infiniteconquest.data.Keyword;

import java.util.*;

/** Five Lands that draw Structures and five Structures that draw Characters for every faction. */
final class FactionTutorExpansion {
    private static final Map<String, List<String>> LAND_NAMES = Map.of(
            "ZEUS", List.of("Stormwright's Approach", "Blinkway Plateau", "Far-Sight Cloudbank", "Keraunic Assembly Field", "Olympian Muster Sky"),
            "POSEIDON", List.of("Mole-Tide Channel", "Vanguard Reef", "Leviathan Mooring Shelf", "Sunken Muster Basin", "Worldsea Anchorage"),
            "HADES", List.of("Shadeburrow Passage", "Grave-Rush Crossing", "Stygian Muster Bank", "Tartarean Calling Ground", "Final Procession"),
            "ARES", List.of("First-Blood Muster", "Siegeborn March", "Ravager's Assembly", "Phalanx War Ground", "Godwar Mobilization"),
            "ATHENA", List.of("Aegis Muster Court", "Owl-Sighted Terrace", "Strategic Assembly", "Pallas Deployment Field", "Perfect Formation Ground"),
            "HEPHAESTUS", List.of("Bronze Muster Yard", "Vanguard Assembly Line", "Siegeframe Proving Ground", "Colossus Foundry Field", "Worldforge Deployment Bay"));

    private static final Map<String, List<String>> STRUCTURE_NAMES = Map.of(
            "ZEUS", List.of("Sparkstep Beacon", "Cloudline Dispatch", "Sharp-Shot Observatory", "Seraphic Relay", "Skyfather's Summons"),
            "POSEIDON", List.of("Undertow Burrow Gate", "Tideguard Barracks", "Nereid Calling Conch", "Leviathan Muster Dock", "Thalassic Hero Hall"),
            "HADES", List.of("Shade Passage Bell", "Graveflash Crypt", "Styx Muster Gate", "Eidolon Calling Hall", "Underworld Legion Vault"),
            "ARES", List.of("Blood-Rush Barracks", "Siegebreaker Arsenal", "Ravager Drum Tower", "Champion's War Hall", "Ares Legion Gate"),
            "ATHENA", List.of("Aegis Cadet Hall", "Owlwatch Roster", "Strategist's Academy", "Pallas Command Archive", "Athena's Heroic Council"),
            "HEPHAESTUS", List.of("Bronze Vanguard Forge", "Siegecrew Workshop", "Automaton Muster Line", "Titanframe Assembly", "Divine Engine Command"));

    private FactionTutorExpansion() { }

    static List<CardDefinition> cards() {
        List<CardDefinition> result = new ArrayList<>();
        for (String faction : FactionDecks.FACTIONS.stream().sorted().toList()) {
            for (int index = 0; index < 5; index++) {
                int cost = 2 + index * 2;
                result.add(tutor(faction, LAND_NAMES.get(faction).get(index), CardType.LAND, cost, index));
                result.add(tutor(faction, STRUCTURE_NAMES.get(faction).get(index), CardType.STRUCTURE, cost, index));
            }
        }
        return List.copyOf(result);
    }

    private static CardDefinition tutor(String faction, String name, CardType type, int cost, int index) {
        int hp = type == CardType.LAND ? Math.min(22, 8 + index * 4) : 10 + index * 4;
        int gpGeneration = 1 + index / 2;
        int activationCost = 2;
        int gold = index < 2 ? 0 : index == 2 ? 1 : 2;
        Keyword specialty = type == CardType.LAND ? switch (faction) {
            case "ZEUS", "ATHENA" -> Keyword.HIGH_GROUND;
            case "POSEIDON" -> Keyword.SANCTUARY;
            case "HADES", "HEPHAESTUS" -> Keyword.ARCHIVE;
            default -> Keyword.WAYSTATION;
        } : switch (faction) {
            case "ZEUS" -> Keyword.WATCHTOWER;
            case "POSEIDON" -> Keyword.MEDIC_TENT;
            case "ATHENA" -> Keyword.BULWARK;
            case "HEPHAESTUS" -> Keyword.WORKSHOP;
            default -> Keyword.BEACON;
        };
        Set<Keyword> keywords = index >= 3 ? Set.of(specialty) : Set.of();
        Map<Keyword, KeywordValue> values = index >= 3 ? Map.of(specialty,
                new KeywordValue(Set.of(Keyword.MEDIC_TENT, Keyword.WORKSHOP, Keyword.BEACON).contains(specialty) ? 1 : 0, 1)) : Map.of();
        Set<String> archetypes = type == CardType.STRUCTURE ? Set.of("RECRUITMENT") : Set.of("MUSTER_GROUND");
        AbilityEffectType effect = type == CardType.LAND
                ? AbilityEffectType.DRAW_STRUCTURE : AbilityEffectType.DRAW_CHARACTER;
        String id = faction.toLowerCase(Locale.ROOT) + "_tutor_"
                + type.name().toLowerCase(Locale.ROOT) + "_" + (index + 1);
        return new CardDefinition(id, name, type, faction, cost, 0, 0, 0, 0, hp,
                keywords, List.of(), gpGeneration, DevelopmentPassive.NONE,
                List.of(new CardAbility(AbilityTrigger.ACTIVATED, effect, 1, activationCost)),
                values, archetypes, gold);
    }
}
