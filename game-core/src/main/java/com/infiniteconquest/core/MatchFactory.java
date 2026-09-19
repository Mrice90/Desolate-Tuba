package com.infiniteconquest.core;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class MatchFactory {
    private final DeckValidator deckValidator = new DeckValidator();

    public GameState create(long seed, MatchRules rules,
                            List<CardDefinition> playerZeroDeck,
                            List<CardDefinition> playerOneDeck) {
        validateDeck(playerZeroDeck, 0);
        validateDeck(playerOneDeck, 1);

        GameState state = new GameState(seed, rules, false);
        loadPlayerDeck(state, seed, 0, playerZeroDeck);
        loadPlayerDeck(state, seed, 1, playerOneDeck);
        state.drawInitialHands();
        state.initializeMatch();
        return state;
    }

    private void validateDeck(List<CardDefinition> deck, int playerId) {
        List<String> errors = deckValidator.validate(deck);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Player " + playerId + " deck is invalid: " + String.join("; ", errors));
        }
    }

    private void loadPlayerDeck(GameState state, long seed, int playerId, List<CardDefinition> definitions) {
        List<CardDefinition> shuffled = new ArrayList<>(definitions);
        Collections.shuffle(shuffled, new Random(derivedSeed(seed, playerId)));

        List<UUID> instanceIds = new ArrayList<>();
        for (int index = 0; index < shuffled.size(); index++) {
            CardDefinition definition = shuffled.get(index);
            UUID instanceId = UUID.nameUUIDFromBytes(
                    (seed + ":" + playerId + ":" + index + ":" + definition.id())
                            .getBytes(StandardCharsets.UTF_8));
            CardInstance instance = new CardInstance(instanceId, definition, playerId, Zone.DECK);
            state.register(instance);
            instanceIds.add(instanceId);
        }
        state.player(playerId).loadDeck(instanceIds);
    }

    private long derivedSeed(long seed, int playerId) {
        return seed ^ (0x9E3779B97F4A7C15L * (playerId + 1L));
    }
}
