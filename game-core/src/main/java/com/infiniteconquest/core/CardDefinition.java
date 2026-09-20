package com.infiniteconquest.core;

import com.infiniteconquest.data.Keyword;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record CardDefinition(
        String id, String name, CardType type, String faction, int cost,
        int attack, int defense, int movement, int range, int hitPoints,
        Set<Keyword> keywords, List<SpellEffect> effects,
        int gpGeneration, DevelopmentPassive developmentPassive, List<CardAbility> abilities
) {
    public CardDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Stable card ID is required");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (cost < 0 || attack < 0 || defense < 0 || movement < 0 || range < 0 || hitPoints < 0 || gpGeneration < 0) {
            throw new IllegalArgumentException("Card numbers cannot be negative");
        }
        if (isPermanent(type) && hitPoints == 0) {
            throw new IllegalArgumentException("Lands, Structures and Capitals require positive HP");
        }
        keywords = keywords == null ? Set.of() : Set.copyOf(keywords);
        effects = effects == null ? List.of() : List.copyOf(effects);
        developmentPassive = developmentPassive == null ? DevelopmentPassive.NONE : developmentPassive;
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
        if (type != CardType.LAND && type != CardType.STRUCTURE
                && (gpGeneration != 0 || developmentPassive != DevelopmentPassive.NONE)) {
            throw new IllegalArgumentException("Only Lands and Structures may generate GP or use development passives");
        }
        if (type == CardType.SPELL && effects.isEmpty()) {
            throw new IllegalArgumentException("Spells require at least one typed effect");
        }
        if (type != CardType.SPELL && !effects.isEmpty()) {
            throw new IllegalArgumentException("Only Spells may define spell effects");
        }
    }

    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range, int hitPoints,
                          Set<Keyword> keywords, List<SpellEffect> effects,
                          int gpGeneration, DevelopmentPassive developmentPassive) {
        this(id, name, type, faction, cost, attack, defense, movement, range, hitPoints,
                keywords, effects, gpGeneration, developmentPassive, List.of());
    }

    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range, int hitPoints,
                          Set<Keyword> keywords, List<SpellEffect> effects) {
        this(id, name, type, faction, cost, attack, defense, movement, range, hitPoints,
                keywords, effects, DevelopmentRules.standardGp(type, cost), DevelopmentPassive.NONE, List.of());
    }

    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range, int hitPoints,
                          Set<Keyword> keywords) {
        this(id, name, type, faction, cost, attack, defense, movement, range, hitPoints, keywords, List.of());
    }

    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range, int hitPoints) {
        this(id, name, type, faction, cost, attack, defense, movement, range, hitPoints, Set.of(), List.of());
    }

    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range) {
        this(id, name, type, faction, cost, attack, defense, movement, range,
                isPermanent(type) ? 1 : 0, Set.of(), List.of());
    }

    public boolean hasKeyword(Keyword keyword) { return keywords.contains(keyword); }
    public boolean isPermanent() { return isPermanent(type); }

    private static boolean isPermanent(CardType type) {
        return type == CardType.LAND || type == CardType.STRUCTURE || type == CardType.CAPITAL;
    }
}
