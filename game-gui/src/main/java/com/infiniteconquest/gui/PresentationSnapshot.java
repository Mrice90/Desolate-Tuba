package com.infiniteconquest.gui;

import com.infiniteconquest.core.*;

import java.util.*;

/** Immutable before/after data for one authoritative command. */
final class PresentationSnapshot {
    enum Change {
        ENTERED_BATTLEFIELD, MOVED, DAMAGED, DESTROYED, ZONE_CHANGED, UNCHANGED
    }

    record CardVisual(UUID id, BoardPosition position, Zone zone, int damage,
                      int hitPoints, int defense, String name, CardType type, boolean top) {
        CardVisual {
            Objects.requireNonNull(id);
            Objects.requireNonNull(zone);
            Objects.requireNonNull(name);
            Objects.requireNonNull(type);
        }
    }

    static final class Frame {
        private final Map<UUID, CardVisual> cards;
        private final long latestEventSequence;

        Frame(Map<UUID, CardVisual> cards) {
            this(cards, -1L);
        }

        Frame(Map<UUID, CardVisual> cards, long latestEventSequence) {
            this.cards = Collections.unmodifiableMap(new LinkedHashMap<>(cards));
            this.latestEventSequence = latestEventSequence;
        }

        Map<UUID, CardVisual> cards() { return cards; }
        CardVisual card(UUID id) { return cards.get(id); }
        long latestEventSequence() { return latestEventSequence; }

        CardVisual topAt(BoardPosition position) {
            return cards.values().stream()
                    .filter(card -> card.top() && Objects.equals(card.position(), position))
                    .findFirst().orElse(null);
        }
    }

    record CardChange(CardVisual before, CardVisual after, Change change) { }

    private final String command;
    private final Frame before;
    private final Frame after;
    private final List<CardChange> changes;
    private final List<GameEvent> events;

    private PresentationSnapshot(String command, Frame before, Frame after, List<GameEvent> events) {
        this.command = Objects.requireNonNull(command);
        this.before = Objects.requireNonNull(before);
        this.after = Objects.requireNonNull(after);
        this.changes = classify(before, after);
        this.events = List.copyOf(events);
    }

    String command() { return command; }
    Frame before() { return before; }
    Frame after() { return after; }
    List<CardChange> changes() { return changes; }
    List<GameEvent> events() { return events; }

    static Frame capture(GameState state) {
        Map<UUID, BoardPosition> positions = new HashMap<>();
        Set<UUID> topCards = new HashSet<>();
        LinkedHashSet<UUID> ids = new LinkedHashSet<>();

        for (BoardPosition position : state.board().positions()) {
            List<UUID> stack = state.board().stackAt(position);
            ids.addAll(stack);
            stack.forEach(id -> positions.put(id, position));
            state.board().topAt(position).ifPresent(topCards::add);
        }
        for (int player = 0; player < 2; player++) {
            ids.addAll(state.player(player).deck());
            ids.addAll(state.player(player).hand());
            ids.addAll(state.player(player).discard());
        }

        Map<UUID, CardVisual> cards = new LinkedHashMap<>();
        for (UUID id : ids) {
            CardInstance card = state.card(id).orElse(null);
            if (card == null) continue;
            CardDefinition definition = card.definition();
            cards.put(id, new CardVisual(id, positions.get(id), card.zone(), card.damage(),
                    definition.hitPoints(), card.effectiveDefense(), definition.name(),
                    definition.type(), topCards.contains(id)));
        }
        long sequence = state.events().stream().mapToLong(GameEvent::sequence).max().orElse(-1L);
        return new Frame(cards, sequence);
    }

    static PresentationSnapshot between(String command, Frame before, GameState afterState) {
        Frame after = capture(afterState);
        List<GameEvent> events = afterState.events().stream()
                .filter(event -> event.sequence() > before.latestEventSequence()).toList();
        return new PresentationSnapshot(command, before, after, events);
    }

    static PresentationSnapshot between(String command, Frame before, Frame after) {
        return new PresentationSnapshot(command, before, after, List.of());
    }

    private static List<CardChange> classify(Frame before, Frame after) {
        LinkedHashSet<UUID> ids = new LinkedHashSet<>(before.cards().keySet());
        ids.addAll(after.cards().keySet());
        List<CardChange> changes = new ArrayList<>();
        for (UUID id : ids) {
            CardVisual old = before.card(id);
            CardVisual current = after.card(id);
            changes.add(new CardChange(old, current, change(old, current)));
        }
        return List.copyOf(changes);
    }

    private static Change change(CardVisual old, CardVisual current) {
        if (old == null && current != null && current.zone() == Zone.BATTLEFIELD)
            return Change.ENTERED_BATTLEFIELD;
        if (old == null || current == null) return Change.ZONE_CHANGED;
        if (old.zone() == Zone.BATTLEFIELD && current.zone() == Zone.DISCARD)
            return Change.DESTROYED;
        if (old.zone() != current.zone()) return current.zone() == Zone.BATTLEFIELD
                ? Change.ENTERED_BATTLEFIELD : Change.ZONE_CHANGED;
        if (!Objects.equals(old.position(), current.position())) return Change.MOVED;
        if (old.damage() != current.damage()) return Change.DAMAGED;
        return Change.UNCHANGED;
    }
}
