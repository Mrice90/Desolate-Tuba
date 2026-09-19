package com.infiniteconquest.core;

import java.util.Objects;

public record CardDefinition(
        String id, String name, CardType type, String faction, int cost,
        int attack, int defense, int movement, int range, int hitPoints
) {
    public CardDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Stable card ID is required");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (cost < 0 || attack < 0 || defense < 0 || movement < 0 || range < 0 || hitPoints < 0) {
            throw new IllegalArgumentException("Card numbers cannot be negative");
        }
        if (isPermanent(type) && hitPoints == 0) {
            throw new IllegalArgumentException("Lands, Structures and Capitals require positive HP");
        }
    }

    /** Compatibility constructor for Characters, Spells, and older development fixtures. */
    public CardDefinition(String id, String name, CardType type, String faction, int cost,
                          int attack, int defense, int movement, int range) {
        this(id, name, type, faction, cost, attack, defense, movement, range,
                isPermanent(type) ? 1 : 0);
    }

    public boolean isPermanent() { return isPermanent(type); }

    private static boolean isPermanent(CardType type) {
        return type == CardType.LAND || type == CardType.STRUCTURE || type == CardType.CAPITAL;
    }
}
