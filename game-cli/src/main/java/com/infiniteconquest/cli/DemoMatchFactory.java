package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DemoMatchFactory {
    private static final List<String> STARTER_IDS = List.of(
            "neo_proto_naiad_recon_droid", "neo_proto_talus_defender",
            "neo_proto_asclepius_medibot", "neo_proto_zephyr_scout",
            "neo_proto_hephaestus_drone", "demo_land_a", "demo_land_b",
            "demo_land_c", "demo_structure_a", "demo_structure_b");

    private final PrototypeCardPool pool = new PrototypeCardPool();

    public GameState create(long seed) {
        List<CardDefinition> deck = demoDeck();
        return create(seed, deck, deck);
    }

    public GameState create(long seed, List<CardDefinition> playerZeroDeck,
                            List<CardDefinition> playerOneDeck) {
        GameState state = new MatchFactory().create(
                seed, MatchRules.current(), playerZeroDeck, playerOneDeck);
        deployCapitals(state, seed);
        return state;
    }

    public List<CardDefinition> demoDeck() {
        List<CardDefinition> deck = new ArrayList<>();
        for (String id : STARTER_IDS) {
            CardDefinition definition = pool.require(id);
            for (int copy = 0; copy < DeckValidator.MAX_COPIES; copy++) deck.add(definition);
        }
        return List.copyOf(deck);
    }

    public PrototypeCardPool pool() { return pool; }

    private void deployCapitals(GameState state, long seed) {
        CapitalDeployment deployment = new CapitalDeployment();
        for (int player = 0; player < 2; player++) {
            CardDefinition definition = new CardDefinition(
                    "demo_capital_p" + player, "Player " + (player + 1) + " Capital",
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
