package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import com.infiniteconquest.core.DeckValidator;

import java.util.*;

public final class FactionDecks {
    public static final Set<String> FACTIONS = Set.of(
            "ZEUS", "POSEIDON", "HADES", "ARES", "ATHENA", "HEPHAESTUS");

    public static final Map<String, CardType> PRIMARY_TYPES = Map.of(
            "ZEUS", CardType.SPELL,
            "POSEIDON", CardType.LAND,
            "HADES", CardType.SPELL,
            "ARES", CardType.CHARACTER,
            "ATHENA", CardType.CHARACTER,
            "HEPHAESTUS", CardType.STRUCTURE);

    public static final Map<String, CardType> SECONDARY_TYPES = Map.of(
            "ZEUS", CardType.CHARACTER,
            "POSEIDON", CardType.CHARACTER,
            "HADES", CardType.CHARACTER,
            "ARES", CardType.SPELL,
            "ATHENA", CardType.STRUCTURE,
            "HEPHAESTUS", CardType.LAND);

    private final PrototypeCardPool pool;

    public FactionDecks(PrototypeCardPool pool) {
        this.pool = pool;
    }

    public List<CardDefinition> starter(String factionName) {
        String faction = factionName.toUpperCase(Locale.ROOT);
        if (!FACTIONS.contains(faction)) throw new IllegalArgumentException("Unknown faction: " + factionName);
        List<CardDefinition> factionCards = pool.cardsForFaction(faction);
        if (factionCards.size() != 35) {
            throw new IllegalStateException(faction + " must contain exactly 35 prototype cards");
        }

        List<CardDefinition> deck = new ArrayList<>(factionCards);
        List<CardDefinition> primaryApex = factionCards.stream()
                .filter(card -> card.id().contains("_apex_"))
                .filter(card -> card.type() == PRIMARY_TYPES.get(faction))
                .toList();
        if (primaryApex.size() != 5) throw new IllegalStateException(faction + " requires five primary apex cards");
        deck.addAll(primaryApex);

        List<String> errors = new DeckValidator().validate(deck);
        if (!errors.isEmpty()) throw new IllegalStateException(String.join("; ", errors));
        return List.copyOf(deck);
    }
}
