package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;
import com.infiniteconquest.core.DeckValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FactionCardSetTest {
    @Test
    void everyFactionHasExactlyFortyUniquePlayableCards() {
        PrototypeCardPool pool = new PrototypeCardPool();

        assertEquals(264, pool.cards().size());
        for (String faction : FactionDecks.FACTIONS) {
            List<CardDefinition> cards = pool.cardsForFaction(faction);
            assertEquals(40, cards.size(), faction);
            assertEquals(40, cards.stream().map(CardDefinition::id).distinct().count(), faction);

            Map<CardType, Long> types = cards.stream()
                    .collect(Collectors.groupingBy(CardDefinition::type, Collectors.counting()));
            assertEquals(40L, types.values().stream().mapToLong(Long::longValue).sum(), faction);
        }
    }

    @Test
    void factionStatsStayInsideTheFirstSetBalanceEnvelope() {
        PrototypeCardPool pool = new PrototypeCardPool();

        for (String faction : FactionDecks.FACTIONS) {
            for (CardDefinition card : pool.cardsForFaction(faction)) {
                int maximumCost = card.id().contains("_apex_") ? 10 : card.id().contains("_keyword_") ? 8 : 7;
                assertTrue(card.cost() >= 0 && card.cost() <= maximumCost, card.id());
                if (card.type() == CardType.CHARACTER) {
                    assertTrue(card.attack() <= card.cost() + 1, card.id() + " attack");
                    assertTrue(card.defense() <= card.cost() + 2, card.id() + " defense");
                    assertTrue(card.range() >= 1 && card.range() <= 3, card.id() + " range");
                    assertTrue(card.movement() >= 1 && card.movement() <= 4, card.id() + " movement");
                } else if (card.type() == CardType.LAND) {
                    assertTrue(card.hitPoints() >= 5 && card.hitPoints() <= (card.id().contains("_apex_") ? 19 : 10), card.id() + " HP");
                } else if (card.type() == CardType.STRUCTURE) {
                    assertTrue(card.hitPoints() >= 5 && card.hitPoints() <= (card.id().contains("_apex_") ? 24 : 13), card.id() + " HP");
                }
            }
        }
    }

    @Test
    void everyFactionStarterUsesFortyUniqueCards() {
        PrototypeCardPool pool = new PrototypeCardPool();
        FactionDecks decks = new FactionDecks(pool);
        DeckValidator validator = new DeckValidator();

        for (String faction : FactionDecks.FACTIONS) {
            List<CardDefinition> deck = decks.starter(faction);
            assertEquals(40, deck.size(), faction);
            assertTrue(validator.isValid(deck), faction);
            Map<String, Long> copies = deck.stream()
                    .collect(Collectors.groupingBy(CardDefinition::id, Collectors.counting()));
            assertEquals(40, copies.size(), faction);
            assertTrue(copies.values().stream().allMatch(count -> count == 1));
        }
    }

    @Test
    void factionIdentityAppearsInImplementedKeywordDistribution() {
        PrototypeCardPool pool = new PrototypeCardPool();

        assertTrue(keywordCount(pool, "ZEUS", "BLINK") >= 3);
        assertTrue(keywordCount(pool, "POSEIDON", "MOLE") >= 2);
        assertTrue(keywordCount(pool, "HADES", "MOLE") >= 3);
        assertTrue(keywordCount(pool, "ARES", "VANGUARD") >= 2);
        assertTrue(keywordCount(pool, "ATHENA", "VANGUARD") >= 5);
        assertTrue(keywordCount(pool, "HEPHAESTUS", "MOLE") >= 3);
    }

    private long keywordCount(PrototypeCardPool pool, String faction, String keyword) {
        return pool.cardsForFaction(faction).stream()
                .filter(card -> card.keywords().stream().anyMatch(value -> value.name().equals(keyword)))
                .count();
    }
}
