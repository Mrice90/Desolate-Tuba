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
    private final Set<String> capitalPassivesUsedThisTurn = new HashSet<>();
    private final CapitalPassiveRules capitalPassiveRules = new CapitalPassiveRules();
    private int activePlayer;
    private int turnNumber;
    private Phase phase = Phase.START;
    private long nextEventSequence;
    private Integer winner;
    private boolean started;
    private boolean initialCapitalPassiveActivated;

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
    public int personalTurnNumber(int id) { return personalTurns[id]; }
    public Phase phase() { return phase; }
    public OptionalInt winner() { return winner == null ? OptionalInt.empty() : OptionalInt.of(winner); }
    public List<GameEvent> events() { return Collections.unmodifiableList(events); }
    public Optional<CardInstance> card(UUID id) { return Optional.ofNullable(cards.get(id)); }
    public List<CardInstance> battlefieldCards(int playerId) {
        return cards.values().stream().filter(card -> card.owner() == playerId && card.zone() == Zone.BATTLEFIELD).toList();
    }
    public Optional<CapitalPassive> capitalPassiveFor(int playerId) {
        return battlefieldCards(playerId).stream()
                .filter(card -> card.definition().type() == CardType.CAPITAL)
                .findFirst().flatMap(card -> capitalPassiveRules.passiveFor(card.definition()));
    }

    public void register(CardInstance card) {
        if (cards.putIfAbsent(card.instanceId(), card) != null) throw new IllegalArgumentException("Duplicate card instance ID");
    }
    void initializeMatch() {
        if (started) throw new IllegalStateException("Match already started");
        started = true; activePlayer = 0; turnNumber = 1; personalTurns[0] = 1;
        emit(GameEvent.Type.MATCH_STARTED, 0, "Match seed " + seed);
        startTurn();
    }
    public void activateInitialCapitalPassive() {
        if (!started || turnNumber != 1 || activePlayer != 0) throw new IllegalStateException("Initial Capital passive timing has passed");
        if (initialCapitalPassiveActivated) throw new IllegalStateException("Initial Capital passive already activated");
        initialCapitalPassiveActivated = true;
        capitalPassiveRules.onTurnStarted(this, activePlayer);
    }
    void drawInitialHands() {
        for (int playerId = 0; playerId < 2; playerId++)
            for (int i = 0; i < rules.initialHandSizeFor(playerId); i++) drawCard(playerId);
    }
    void advanceTurn() {
        phase = Phase.END;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "END");
        emit(GameEvent.Type.TURN_ENDED, activePlayer, "Turn ended");
        if (turnNumber >= rules.conquestDeadlineTurn()) {
            resolveConquestDeadline();
            return;
        }
        activePlayer = 1 - activePlayer; turnNumber++; personalTurns[activePlayer]++;
        startTurn();
    }
    void recordCardPlayed(CardInstance card) {
        emit(GameEvent.Type.CARD_PLAYED, card.owner(), card.instanceId().toString());
        capitalPassiveRules.onCardPlayed(this, card);
    }
    void recordCharacterMoved(CardInstance card, BoardPosition from, BoardPosition to, int distance) {
        emit(GameEvent.Type.CHARACTER_MOVED, card.owner(), card.instanceId() + " " + from + " -> " + to + " cost " + distance);
    }
    void recordAttack(CardInstance attacker, CardInstance target) {
        emit(GameEvent.Type.ATTACK_RESOLVED, attacker.owner(), attacker.instanceId() + " -> " + target.instanceId());
    }
    void drawCards(int playerId, int amount) {
        for (int i = 0; i < amount; i++) drawCard(playerId);
    }
    void returnCharacterToHand(CardInstance card) {
        board.remove(card.instanceId());
        card.moveTo(Zone.HAND);
        player(card.owner()).addToHand(card.instanceId());
    }
    void destroy(CardInstance card) {
        boolean permanent = card.definition().isPermanent();
        BoardPosition formerPosition = board.positionOf(card.instanceId()).orElse(null);
        board.remove(card.instanceId());
        card.moveTo(Zone.DISCARD);
        player(card.owner()).addToDiscard(card.instanceId());
        emit(GameEvent.Type.CARD_DESTROYED, card.owner(), card.instanceId().toString());
        if (permanent) capitalPassiveRules.onPermanentDestroyed(this, card);
        if (permanent) {
            int result = new VictoryEvaluator().winnerAfterPermanentLoss(this, card.owner());
            if (result >= 0) {
                finishGame(result, "Player " + result + " wins");
            }
        }
        if (phase != Phase.GAME_OVER && formerPosition != null) {
            board.topAt(formerPosition).flatMap(this::card)
                    .filter(revealed -> revealed.definition().isPermanent())
                    .filter(revealed -> revealed.damage() >= revealed.definition().hitPoints())
                    .ifPresent(this::destroy);
        }
    }

    private void startTurn() {
        capitalPassivesUsedThisTurn.clear();
        phase = Phase.START;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "START");
        player(activePlayer).startTurnWithGp(rules.gpForTurn(activePlayer, personalTurns[activePlayer]));
        resetControlledCards(activePlayer);
        for (int i = 0; i < rules.cardsDrawnAtTurnStart(); i++) drawCard(activePlayer);
        if (phase == Phase.GAME_OVER) return;
        capitalPassiveRules.onTurnStarted(this, activePlayer);
        if (phase == Phase.GAME_OVER) return;
        applyConquestPressure();
        if (phase == Phase.GAME_OVER) return;
        emit(GameEvent.Type.TURN_STARTED, activePlayer, "Personal turn " + personalTurns[activePlayer]);
        phase = Phase.PLAY;
        emit(GameEvent.Type.PHASE_CHANGED, activePlayer, "PLAY");
    }
    private void resetControlledCards(int playerId) {
        int untapped = 0;
        for (CardInstance card : cards.values()) if (card.owner() == playerId && card.zone() == Zone.BATTLEFIELD) {
            if (card.tapped()) untapped++;
            card.resetTurnActions();
        }
        emit(GameEvent.Type.CARDS_UNTAPPED, playerId, Integer.toString(untapped));
    }
    private void drawCard(int playerId) {
        Optional<UUID> drawn = player(playerId).drawOne();
        if (drawn.isEmpty()) {
            emit(GameEvent.Type.DRAW_FAILED, playerId, "Deck is empty");
            for (CardInstance card : cards.values()) if (card.owner() == playerId && card.zone() == Zone.BATTLEFIELD && card.definition().isPermanent()) {
                card.addDamage(1);
                emit(GameEvent.Type.EXHAUSTION_DAMAGE, playerId, card.instanceId().toString());
                if (card.damage() >= card.definition().hitPoints()
                        && board.positionOf(card.instanceId()).flatMap(board::topAt)
                        .filter(card.instanceId()::equals).isPresent()) destroy(card);
                if (phase == Phase.GAME_OVER) break;
            }
            return;
        }
        CardInstance instance = cards.get(drawn.orElseThrow());
        if (instance == null) throw new IllegalStateException("Deck references unregistered card");
        instance.moveTo(Zone.HAND);
        emit(GameEvent.Type.CARD_DRAWN, playerId, instance.instanceId().toString());
    }
    Optional<CardInstance> returnMostRecentDiscardedCharacter(int playerId) {
        Optional<UUID> id = player(playerId).removeMostRecentDiscard(value -> card(value)
                .map(card -> card.definition().type() == CardType.CHARACTER).orElse(false));
        if (id.isEmpty()) return Optional.empty();
        CardInstance returned = card(id.orElseThrow()).orElseThrow();
        returned.moveTo(Zone.HAND);
        player(playerId).addToHand(returned.instanceId());
        return Optional.of(returned);
    }
    boolean tryUseCapitalPassive(int playerId, CapitalPassive passive) {
        return capitalPassivesUsedThisTurn.add(playerId + ":" + passive.name());
    }
    void markCapitalPassiveUsed(int playerId, CapitalPassive passive) {
        capitalPassivesUsedThisTurn.add(playerId + ":" + passive.name());
    }
    void recordCapitalPassive(int playerId, CapitalPassive passive, String detail) {
        emit(GameEvent.Type.CAPITAL_PASSIVE_TRIGGERED, playerId, passive.name() + ": " + detail);
    }
    private void applyConquestPressure() {
        int damage = rules.conquestPressureDamage(turnNumber);
        if (damage == 0) return;
        List<CardInstance> controlledPermanents = new ArrayList<>(battlefieldCards(activePlayer).stream()
                .filter(card -> card.definition().isPermanent()).toList());
        for (CardInstance card : controlledPermanents) {
            if (card.zone() != Zone.BATTLEFIELD) continue;
            card.addDamage(damage);
            emit(GameEvent.Type.CONQUEST_PRESSURE, activePlayer,
                    card.instanceId() + " takes " + damage + " damage");
            if (card.damage() >= card.definition().hitPoints()
                    && board.positionOf(card.instanceId()).flatMap(board::topAt)
                    .filter(card.instanceId()::equals).isPresent()) destroy(card);
            if (phase == Phase.GAME_OVER) return;
        }
    }
    private void resolveConquestDeadline() {
        int firstCount = permanentCount(0);
        int secondCount = permanentCount(1);
        if (firstCount != secondCount) {
            int result = firstCount > secondCount ? 0 : 1;
            finishGame(result, "Conquest deadline: " + firstCount + " permanents to " + secondCount);
            return;
        }
        int firstHealth = remainingPermanentHealth(0);
        int secondHealth = remainingPermanentHealth(1);
        if (firstHealth != secondHealth) {
            int result = firstHealth > secondHealth ? 0 : 1;
            finishGame(result, "Conquest deadline: " + firstHealth + " health to " + secondHealth);
            return;
        }
        finishGame(null, "Conquest deadline draw");
    }
    private int permanentCount(int playerId) {
        return (int) battlefieldCards(playerId).stream().filter(card -> card.definition().isPermanent()).count();
    }
    private int remainingPermanentHealth(int playerId) {
        return battlefieldCards(playerId).stream().filter(card -> card.definition().isPermanent())
                .mapToInt(card -> Math.max(0, card.definition().hitPoints() - card.damage())).sum();
    }
    private void finishGame(Integer winningPlayer, String detail) {
        winner = winningPlayer;
        phase = Phase.GAME_OVER;
        emit(GameEvent.Type.GAME_OVER, winningPlayer == null ? -1 : winningPlayer, detail);
    }
    private void emit(GameEvent.Type type, int playerId, String detail) {
        events.add(new GameEvent(nextEventSequence++, turnNumber, playerId, type, detail));
    }
}
