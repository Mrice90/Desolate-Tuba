package com.infiniteconquest.core;

import java.util.*;

public final class BoardState {
    private final Map<BoardPosition, List<UUID>> cells = new LinkedHashMap<>();

    public BoardState() {
        for (int y = 0; y < BoardPosition.HEIGHT; y++) {
            for (int x = 0; x < BoardPosition.WIDTH; x++) {
                cells.put(new BoardPosition(x, y), new ArrayList<>());
            }
        }
    }

    public List<UUID> stackAt(BoardPosition position) {
        return Collections.unmodifiableList(cells.get(Objects.requireNonNull(position)));
    }

    public Optional<UUID> topAt(BoardPosition position) {
        List<UUID> stack = cells.get(Objects.requireNonNull(position));
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack.get(stack.size() - 1));
    }

    public boolean isEmpty(BoardPosition position) {
        return cells.get(Objects.requireNonNull(position)).isEmpty();
    }

    public void push(BoardPosition position, UUID instanceId) {
        Objects.requireNonNull(instanceId);
        if (cells.values().stream().anyMatch(stack -> stack.contains(instanceId))) {
            throw new IllegalStateException("Card instance is already on the battlefield");
        }
        cells.get(Objects.requireNonNull(position)).add(instanceId);
    }

    public UUID pop(BoardPosition position) {
        List<UUID> stack = cells.get(Objects.requireNonNull(position));
        if (stack.isEmpty()) throw new IllegalStateException("Cannot pop an empty battlefield cell");
        return stack.remove(stack.size() - 1);
    }
}
