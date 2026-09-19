package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import com.infiniteconquest.core.DeckValidator;
import com.infiniteconquest.data.Keyword;

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

    public static final Map<String, Keyword> PRIMARY_KEYWORDS = Map.of(
            "ZEUS", Keyword.BLINK,
            "POSEIDON", Keyword.MOLE,
            "HADES", Keyword.MOLE,
            "ARES", Keyword.VANGUARD,
            "ATHENA", Keyword.VANGUARD,
            "HEPHAESTUS", Keyword.VANGUARD);

    public static final Map<String, Keyword> SECONDARY_KEYWORDS = Map.of(
            "ZEUS", Keyword.VANGUARD,
            "POSEIDON", Keyword.VANGUARD,
            "HADES", Keyword.BLINK,
            "ARES", Keyword.BLINK,
            "ATHENA", Keyword.MOLE,
            "HEPHAESTUS", Keyword.MOLE);

    private final PrototypeCardPool pool;

    public FactionDecks(PrototypeCardPool pool) {
        this.pool = pool;
    }

    public List<CardDefinition> starter(String factionName) {
        String faction = factionName.toUpperCase(Locale.ROOT);
        if (!FACTIONS.contains(faction)) throw new IllegalArgumentException("Unknown faction: " + factionName);
        List<CardDefinition> factionCards = pool.cardsForFaction(faction);
        if (factionCards.size() != DeckValidator.REQUIRED_SIZE) {
            throw new IllegalStateException(faction + " must contain exactly 40 prototype cards");
        }

        List<CardDefinition> deck = new ArrayList<>(factionCards);

        List<String> errors = new DeckValidator().validate(deck);
        if (!errors.isEmpty()) throw new IllegalStateException(String.join("; ", errors));
        return List.copyOf(deck);
    }
}
