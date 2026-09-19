package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import com.infiniteconquest.core.DeckValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class FactionDecks {
    public static final Set<String> FACTIONS = Set.of(
            "ZEUS", "POSEIDON", "HADES", "ARES", "ATHENA", "HEPHAESTUS");

    private final PrototypeCardPool pool;

    public FactionDecks(PrototypeCardPool pool) {
        this.pool = pool;
    }

    public List<CardDefinition> starter(String factionName) {
        String faction = factionName.toUpperCase(Locale.ROOT);
        if (!FACTIONS.contains(faction)) throw new IllegalArgumentException("Unknown faction: " + factionName);
        List<CardDefinition> factionCards = pool.cardsForFaction(faction);
        if (factionCards.size() != 25) {
            throw new IllegalStateException(faction + " must contain exactly 25 prototype cards");
        }

        List<CardDefinition> deck = new ArrayList<>(factionCards);
        addSecondCopies(deck, factionCards, CardType.CHARACTER, 8);
        addSecondCopies(deck, factionCards, CardType.LAND, 3);
        addSecondCopies(deck, factionCards, CardType.STRUCTURE, 2);
        addSecondCopies(deck, factionCards, CardType.SPELL, 2);

        List<String> errors = new DeckValidator().validate(deck);
        if (!errors.isEmpty()) throw new IllegalStateException(String.join("; ", errors));
        return List.copyOf(deck);
    }

    private void addSecondCopies(List<CardDefinition> deck, List<CardDefinition> pool,
                                 CardType type, int count) {
        pool.stream().filter(card -> card.type() == type).limit(count).forEach(deck::add);
    }
}
