package com.infiniteconquest.core;

import java.util.*;

public final class PlayerState {
    private final int id;
    private final List<UUID> deck = new ArrayList<>();
    private final List<UUID> hand = new ArrayList<>();
    private final List<UUID> discard = new ArrayList<>();
    private int currentGp;
    private int maximumGp;

    public PlayerState(int id) {
        if (id < 0 || id > 1) throw new IllegalArgumentException("Player ID must be 0 or 1");
        this.id = id;
    }

    public int id() { return id; }
    public List<UUID> deck() { return Collections.unmodifiableList(deck); }
    public List<UUID> hand() { return Collections.unmodifiableList(hand); }
    public List<UUID> discard() { return Collections.unmodifiableList(discard); }
    public int currentGp() { return currentGp; }
    public int maximumGp() { return maximumGp; }

    public void addToHand(UUID id) { hand.add(Objects.requireNonNull(id)); }
    public boolean hasInHand(UUID id) { return hand.contains(id); }
    public void removeFromHand(UUID id) {
        if (!hand.remove(id)) throw new IllegalStateException("Card is not in hand");
    }
    public void spendGp(int amount) {
        if (amount < 0 || amount > currentGp) throw new IllegalArgumentException("Insufficient GP");
        currentGp -= amount;
    }
    public void beginTurn() {
        maximumGp = Math.min(10, maximumGp + 1);
        currentGp = maximumGp;
    }
}
