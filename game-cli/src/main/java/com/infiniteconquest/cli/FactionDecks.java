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
            "ARES", Keyword.FAST_STRIKE,
            "ATHENA", Keyword.VANGUARD,
            "HEPHAESTUS", Keyword.VANGUARD);

    public static final Map<String, Keyword> SECONDARY_KEYWORDS = Map.of(
            "ZEUS", Keyword.SHARP_SHOT,
            "POSEIDON", Keyword.VANGUARD,
            "HADES", Keyword.FAST_STRIKE,
            "ARES", Keyword.SIEGE,
            "ATHENA", Keyword.SHARP_SHOT,
            "HEPHAESTUS", Keyword.SIEGE);

    private final PrototypeCardPool pool;

    public FactionDecks(PrototypeCardPool pool) {
        this.pool = pool;
    }

    public List<CardDefinition> starter(String factionName) {
        String faction = factionName.toUpperCase(Locale.ROOT);
        if (!FACTIONS.contains(faction)) throw new IllegalArgumentException("Unknown faction: " + factionName);
        List<CardDefinition> factionCards = pool.cardsForFaction(faction);
        List<CardDefinition> developments = new ArrayList<>();
        for (CardType type : List.of(CardType.LAND, CardType.STRUCTURE)) {
            for (int tier = 1; tier <= 10; tier++) {
                int requiredTier = tier;
                factionCards.stream()
                        .filter(card -> card.type() == type && card.cost() == requiredTier)
                        .filter(card -> card.id().contains("_ramp_"))
                        .min(Comparator.comparing(CardDefinition::name))
                        .ifPresent(developments::add);
            }
            factionCards.stream()
                    .filter(card -> card.type() == type && !card.id().contains("_ramp_"))
                    .sorted(Comparator.comparingInt(CardDefinition::cost).thenComparing(CardDefinition::name))
                    .limit(8).forEach(developments::add);
        }
        List<CardDefinition> actions = factionCards.stream()
                .filter(card -> card.type() != CardType.LAND && card.type() != CardType.STRUCTURE)
                .filter(card -> card.keywords().isEmpty() || card.keywords().stream().allMatch(keyword ->
                        keyword == PRIMARY_KEYWORDS.get(faction) || keyword == SECONDARY_KEYWORDS.get(faction)))
                .toList();
        final int starterSize = 60;
        if (developments.size() != 36 || developments.size() + actions.size() < starterSize) {
            throw new IllegalStateException(faction + " does not have a valid 60-card starter pool");
        }
        int actionSlots = starterSize - developments.size();
        List<CardDefinition> tactical = actions.stream().filter(this::hasNewTacticalKeyword).toList();
        if (tactical.size() > actionSlots) throw new IllegalStateException(faction + " has too many required tactical cards");
        List<CardDefinition> deck = new ArrayList<>(developments);
        deck.addAll(tactical);
        actions.stream().filter(card -> !hasNewTacticalKeyword(card))
                .limit(actionSlots - tactical.size()).forEach(deck::add);
        for (int index = 0; deck.size() < starterSize; index++) {
            CardDefinition candidate = actions.get(index % actions.size());
            long copies = deck.stream().filter(card -> card.id().equals(candidate.id())).count();
            if (copies < DeckValidator.MAX_COPIES) deck.add(candidate);
        }

        List<String> errors = new DeckValidator().validate(deck);
        if (!errors.isEmpty()) throw new IllegalStateException(String.join("; ", errors));
        return List.copyOf(deck);
    }

    private boolean hasNewTacticalKeyword(CardDefinition card) {
        return card.hasKeyword(Keyword.FAST_STRIKE) || card.hasKeyword(Keyword.SIEGE)
                || card.hasKeyword(Keyword.SHARP_SHOT);
    }
}
