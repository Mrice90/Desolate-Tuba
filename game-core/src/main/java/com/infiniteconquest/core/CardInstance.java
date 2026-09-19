package com.infiniteconquest.core;

import java.util.Objects;
import java.util.UUID;

public final class CardInstance {
    private final UUID instanceId;
    private final CardDefinition definition;
    private final int owner;
    private Zone zone;
    private int damage;
    private boolean tapped;
    private int movementSpent;
    private boolean attackedThisTurn;
    private boolean blinkUsedThisTurn;

    public CardInstance(UUID instanceId, CardDefinition definition, int owner, Zone zone) {
        this.instanceId = Objects.requireNonNull(instanceId);
        this.definition = Objects.requireNonNull(definition);
        if (owner < 0 || owner > 1) throw new IllegalArgumentException("Owner must be player 0 or 1");
        this.owner = owner;
        this.zone = Objects.requireNonNull(zone);
    }

    public UUID instanceId() { return instanceId; }
    public CardDefinition definition() { return definition; }
    public int owner() { return owner; }
    public Zone zone() { return zone; }
    public int damage() { return damage; }
    public boolean tapped() { return tapped; }
    public int movementSpent() { return movementSpent; }
    public int movementRemaining() { return Math.max(0, definition.movement() - movementSpent); }
    public boolean attackedThisTurn() { return attackedThisTurn; }
    public boolean blinkUsedThisTurn() { return blinkUsedThisTurn; }
    public void moveTo(Zone newZone) { zone = Objects.requireNonNull(newZone); }
    public void addDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Damage cannot be negative");
        damage += amount;
    }
    public void setTapped(boolean value) { tapped = value; }
    public void spendMovement(int amount) {
        if (amount < 0 || amount > movementRemaining()) throw new IllegalArgumentException("Insufficient movement");
        movementSpent += amount;
    }
    public void markAttacked() { attackedThisTurn = true; }
    public void markBlinkUsed() { blinkUsedThisTurn = true; }
    public void resetTurnActions() {
        movementSpent = 0;
        attackedThisTurn = false;
        blinkUsedThisTurn = false;
        tapped = false;
    }
}
