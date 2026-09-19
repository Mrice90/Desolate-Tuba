package com.infiniteconquest.core;

import java.util.*;

public final class GameState {
    private final long seed;
    private final BoardState board = new BoardState();
    private final List<PlayerState> players = List.of(new PlayerState(0), new PlayerState(1));
    private final Map<UUID, CardInstance> cards = new LinkedHashMap<>();
    private int activePlayer;
    private int turnNumber = 1;
    private Phase phase = Phase.PLAY;

    public GameState(long seed) {
        this.seed = seed;
        players.get(0).beginTurn();
    }

    public long seed() { return seed; }
    public BoardState board() { return board; }
    public PlayerState player(int id) { return players.get(id); }
    public int activePlayer() { return activePlayer; }
    public int turnNumber() { return turnNumber; }
    public Phase phase() { return phase; }
    public Optional<CardInstance> card(UUID id) { return Optional.ofNullable(cards.get(id)); }

    public void register(CardInstance card) {
        if (cards.putIfAbsent(card.instanceId(), card) != null) {
            throw new IllegalArgumentException("Duplicate card instance ID");
        }
    }

    void advanceTurn() {
        phase = Phase.END;
        activePlayer = 1 - activePlayer;
        turnNumber++;
        players.get(activePlayer).beginTurn();
        phase = Phase.PLAY;
    }
}
