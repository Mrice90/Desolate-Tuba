package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.DeckValidator;

import java.util.*;

public final class DeckEditor {
    private final PrototypeCardPool pool;
    private final List<CardDefinition> cards;

    public DeckEditor(PrototypeCardPool pool, List<CardDefinition> startingDeck) {
        this.pool = Objects.requireNonNull(pool);
        this.cards = new ArrayList<>(Objects.requireNonNull(startingDeck));
    }

    public List<CardDefinition> cards() { return Collections.unmodifiableList(cards); }

    public void reset(List<CardDefinition> replacement) {
        cards.clear();
        cards.addAll(Objects.requireNonNull(replacement));
    }

    public Map<String, Long> counts() {
        Map<String, Long> result = new TreeMap<>();
        for (CardDefinition card : cards) result.merge(card.id(), 1L, Long::sum);
        return Collections.unmodifiableMap(result);
    }

    public void add(String id) {
        CardDefinition card = pool.require(id);
        long copies = cards.stream().filter(existing -> existing.id().equals(id)).count();
        if (copies >= DeckValidator.MAX_COPIES) {
            throw new IllegalArgumentException(id + " already has four copies");
        }
        if (cards.size() >= DeckValidator.REQUIRED_SIZE) {
            throw new IllegalArgumentException("Remove a card before adding another; deck already has 40 cards");
        }
        cards.add(card);
    }

    public void remove(String id) {
        int index = -1;
        for (int i = 0; i < cards.size(); i++) if (cards.get(i).id().equals(id)) { index = i; break; }
        if (index < 0) throw new IllegalArgumentException(id + " is not in the deck");
        cards.remove(index);
    }

    public void swap(String removeId, String addId) {
        pool.require(addId);
        long addCopies = cards.stream().filter(card -> card.id().equals(addId)).count();
        if (addCopies >= DeckValidator.MAX_COPIES) {
            throw new IllegalArgumentException(addId + " already has four copies");
        }
        int removeIndex = -1;
        for (int i = 0; i < cards.size(); i++) if (cards.get(i).id().equals(removeId)) { removeIndex = i; break; }
        if (removeIndex < 0) throw new IllegalArgumentException(removeId + " is not in the deck");
        cards.set(removeIndex, pool.require(addId));
    }

    public List<String> validationErrors() {
        return new DeckValidator().validate(cards);
    }
}
