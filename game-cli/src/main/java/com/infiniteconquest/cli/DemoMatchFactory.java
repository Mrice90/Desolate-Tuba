package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;
import com.infiniteconquest.data.CardCatalog;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DemoMatchFactory {
    public GameState create(long seed) {
        List<CardDefinition> deck = demoDeck();
        GameState state = new MatchFactory().create(seed, MatchRules.current(), deck, deck);
        deployCapitals(state, seed);
        return state;
    }

    public List<CardDefinition> demoDeck() {
        List<CardDefinition> definitions = new ArrayList<>(
                CardCatalog.loadResource("/cards/prototype-characters.json").definitions());
        definitions.add(new CardDefinition("demo_land_a", "Aether Field", CardType.LAND,
                "DEMO", 0, 0, 0, 0, 0, 6));
        definitions.add(new CardDefinition("demo_land_b", "Forge District", CardType.LAND,
                "DEMO", 1, 0, 0, 0, 0, 8));
        definitions.add(new CardDefinition("demo_land_c", "Moonlit Grove", CardType.LAND,
                "DEMO", 0, 0, 0, 0, 0, 5));
        definitions.add(new CardDefinition("demo_structure_a", "Watchtower", CardType.STRUCTURE,
                "DEMO", 2, 0, 0, 0, 0, 6));
        definitions.add(new CardDefinition("demo_structure_b", "Aegis Relay", CardType.STRUCTURE,
                "DEMO", 3, 0, 0, 0, 0, 9));

        List<CardDefinition> deck = new ArrayList<>();
        for (CardDefinition definition : definitions) {
            for (int copy = 0; copy < 4; copy++) deck.add(definition);
        }
        return List.copyOf(deck);
    }

    private void deployCapitals(GameState state, long seed) {
        CapitalDeployment deployment = new CapitalDeployment();
        for (int player = 0; player < 2; player++) {
            CardDefinition definition = new CardDefinition(
                    "demo_capital_p" + player, "Player " + player + " Capital",
                    CardType.CAPITAL, "DEMO", 0, 0, 0, 0, 0, 20);
            UUID id = UUID.nameUUIDFromBytes(
                    (seed + ":capital:" + player).getBytes(StandardCharsets.UTF_8));
            CardInstance capital = new CardInstance(id, definition, player, Zone.DECK);
            state.register(capital);
            deployment.commit(player, capital,
                    new BoardPosition(player == 0 ? 1 : 2, player == 0 ? 0 : 5));
        }
        deployment.reveal(state.board());
    }
}
