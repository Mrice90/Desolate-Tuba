package com.infiniteconquest.core;

import java.util.*;

public final class GameState {
    private final long seed;
    private final MatchRules rules;
    private final BoardState board = new BoardState();
    private final List<PlayerState> players = List.of(new PlayerState(0), new PlayerState(1));
    private final Map<UUID, CardInstance> cards = new LinkedHashMap<>();
    private final List<GameEvent> events = new ArrayList<>();
    private final int[] personalTurns = new int[2];
    private int activePlayer;
    private int turnNumber;
    private Phase phase = Phase.START;
    private long nextEventSequence;
    private boolean started;

    public GameState(long seed) { this(seed, MatchRules.current(), true); }
    GameState(long seed, MatchRules rules, boolean startImmediately) {
        this.seed = seed;
        this.rules = Objects.requireNonNull(rules);
        if (startImmediately) initializeMatch();
    }

    public long seed() { return seed; }
    public MatchRules rules() { return rules; }
    public BoardState board() { return board; }
    public PlayerState player(int id) { return players.get(id); }
    public int activePlayer() { return activePlayer; }
    public int turnNumber() { return turnNumber; }
    public int personalTurnNumber(int playerId) { return personalTurns[playerId]; }
    public Phase phase() { return phase; }
    public List<GameEvent> events() { return Collections.unmodifiableList(events); }
    public Optional<CardInstance> card(UUID id) { return Optional.ofNullable(cards.get(id)); }

    public void register(CardInstance card) {
        if (cards.putIfAbsent(card.instanceId(), card) != null) throw new IllegalArgumentException("Duplicate card instance ID");
    }

    void initializeMatch() {
        if (started) throw new IllegalStateException("Match already started");
        started = true;
        activePlayer = 0;
        turnNumber = 1;
        personalTurns[0] = 1;
        emit(GameEvent.Type.MATCH_STARTED, 0, "Match seed " + seed);
        startTurn();
    }

    void drawInitialHands() {
        for (int playerId = 0; playerId < 2; playerId++) {
            for (int i = 0; i < rules.initialHandSize(); i++) drawCard(playerId);
        }
    }

    void advanceTurn() {
        requireStarted();
        phase = Phase.END;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "END");
        emit(GameEvent.Type.TURN_ENDED, activePlayer, "Turn ended");
        activePlayer = 1 - activePlayer;
        turnNumber++;
        personalTurns[activePlayer]++;
        startTurn();
    }

    void recordCardPlayed(CardInstance card) {
        emit(GameEvent.Type.CARD_PLAYED, card.owner(), card.instanceId().toString());
    }

    private void startTurn() {
        phase = Phase.START;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "START");
        player(activePlayer).startTurnWithGp(rules.gpForTurn(activePlayer, personalTurns[activePlayer]));
        untapControlledCards(activePlayer);
        for (int i = 0; i < rules.cardsDrawnAtTurnStart(); i++) drawCard(activePlayer);
        emit(GameEvent.Type.TURN_STARTED, activePlayer,
                "Personal turn " + personalTurns[activePlayer] + ", GP " + player(activePlayer).currentGp());
        phase = Phase.PLAY;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "PLAY");
    }

    private void untapControlledCards(int playerId) {
        int untapped = 0;
        for (CardInstance card : cards.values()) {
            if (card.owner() == playerId && card.zone() == Zone.BATTLEFIELD && card.tapped()) {
                card.setTapped(false);
                untapped++;
            }
        }
        emit(GameEvent.Type.CARDS_UNTAPPED, playerId, Integer.toString(untapped));
    }

    private void drawCard(int playerId) {
        Optional<UUID> drawn = player(playerId).drawOne();
        if (drawn.isEmpty()) {
            emit(GameEvent.Type.DRAW_FAILED, playerId, "Deck is empty");
            applyExhaustionDamage(playerId);
            return;
        }
        CardInstance instance = cards.get(drawn.orElseThrow());
        if (instance == null) throw new IllegalStateException("Deck references an unregistered card");
        instance.moveTo(Zone.HAND);
        emit(GameEvent.Type.CARD_DRAWN, playerId, instance.instanceId().toString());
    }

    private void applyExhaustionDamage(int playerId) {
        for (CardInstance card : cards.values()) {
            if (card.owner() == playerId && card.zone() == Zone.BATTLEFIELD && isPermanent(card.definition().type())) {
                card.addDamage(1);
                emit(GameEvent.Type.EXHAUSTION_DAMAGE, playerId, card.instanceId().toString());
            }
        }
    }

    private boolean isPermanent(CardType type) {
        return type == CardType.LAND || type == CardType.STRUCTURE || type == CardType.CAPITAL;
    }

    private void emit(GameEvent.Type type, int playerId, String detail) {
        events.add(new GameEvent(nextEventSequence++, turnNumber, playerId, type, detail));
    }
    private void requireStarted() {
        if (!started) throw new IllegalStateException("Match has not started");
    }
}
