package com.infiniteconquest.core;

import java.util.Objects;

public record CardDefinition(
        String id,
        String name,
        CardType type,
        String faction,
        int cost,
        int attack,
        int defense,
        int movement,
        int range
) {
    public CardDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Stable card ID is required");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        if (cost < 0 || attack < 0 || defense < 0 || movement < 0 || range < 0) {
            throw new IllegalArgumentException("Card numbers cannot be negative");
        }
    }
}
