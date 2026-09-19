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
    private final CapitalRoster capitals = new CapitalRoster();

    public GameState create(long seed) {
        List<CardDefinition> deck = demoDeck();
        return create(seed, deck, deck);
    }

    public GameState create(long seed, List<CardDefinition> playerZeroDeck,
                            List<CardDefinition> playerOneDeck) {
        CardDefinition playerZeroCapital = capitals.defaultForDeck(playerZeroDeck).orElse(null);
        CardDefinition playerOneCapital = capitals.defaultForDeck(playerOneDeck).orElse(null);
        return create(seed, playerZeroDeck, playerOneDeck, playerZeroCapital, playerOneCapital);
    }

    public GameState create(long seed, List<CardDefinition> playerZeroDeck,
                            List<CardDefinition> playerOneDeck,
                            CardDefinition playerZeroCapital, CardDefinition playerOneCapital) {
        validateCapitalChoice(playerZeroDeck, playerZeroCapital);
        validateCapitalChoice(playerOneDeck, playerOneCapital);
        GameState state = new MatchFactory().create(
                seed, MatchRules.current(), playerZeroDeck, playerOneDeck);
        deployCapitals(state, seed, playerZeroCapital, playerOneCapital);
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
    public CapitalRoster capitals() { return capitals; }

    private void validateCapitalChoice(List<CardDefinition> deck, CardDefinition capital) {
        if (capital == null) return;
        if (capital.type() != CardType.CAPITAL) throw new IllegalArgumentException("Selected card is not a Capital");
        java.util.Set<String> factions = deck.stream().map(CardDefinition::faction)
                .filter(FactionDecks.FACTIONS::contains).collect(java.util.stream.Collectors.toSet());
        if (factions.size() == 1 && !factions.contains(capital.faction())) {
            throw new IllegalArgumentException("Capital faction must match the deck faction");
        }
    }

    private void deployCapitals(GameState state, long seed,
                                CardDefinition playerZeroCapital, CardDefinition playerOneCapital) {
        CapitalDeployment deployment = new CapitalDeployment();
        for (int player = 0; player < 2; player++) {
            CardDefinition selected = player == 0 ? playerZeroCapital : playerOneCapital;
            CardDefinition definition = selected != null ? selected : new CardDefinition(
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
