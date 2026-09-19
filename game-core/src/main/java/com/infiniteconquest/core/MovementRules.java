package com.infiniteconquest.core;

import java.util.*;

public final class MovementRules {
    public Set<BoardPosition> legalDestinations(GameState state, CardInstance character) {
        if (character.definition().type() != CardType.CHARACTER || character.zone() != Zone.BATTLEFIELD) return Set.of();
        Optional<BoardPosition> originResult = state.board().positionOf(character.instanceId());
        if (originResult.isEmpty() || !state.board().topAt(originResult.get()).orElseThrow().equals(character.instanceId())) return Set.of();

        int allowance = character.movementRemaining();
        if (allowance == 0) return Set.of();
        BoardPosition origin = originResult.get();
        Map<BoardPosition, Integer> distance = new HashMap<>();
        ArrayDeque<BoardPosition> queue = new ArrayDeque<>();
        distance.put(origin, 0);
        queue.add(origin);

        while (!queue.isEmpty()) {
            BoardPosition current = queue.removeFirst();
            int nextDistance = distance.get(current) + 1;
            if (nextDistance > allowance) continue;
            for (BoardPosition next : neighbors(current)) {
                if (!state.board().isEmpty(next) || distance.containsKey(next)) continue;
                distance.put(next, nextDistance);
                queue.addLast(next);
            }
        }
        distance.remove(origin);
        return Collections.unmodifiableSet(distance.keySet());
    }

    public int shortestLegalDistance(GameState state, CardInstance character, BoardPosition destination) {
        BoardPosition origin = state.board().positionOf(character.instanceId()).orElseThrow();
        if (!legalDestinations(state, character).contains(destination)) return -1;

        Map<BoardPosition, Integer> distance = new HashMap<>();
        ArrayDeque<BoardPosition> queue = new ArrayDeque<>();
        distance.put(origin, 0);
        queue.add(origin);
        while (!queue.isEmpty()) {
            BoardPosition current = queue.removeFirst();
            if (current.equals(destination)) return distance.get(current);
            for (BoardPosition next : neighbors(current)) {
                if ((!state.board().isEmpty(next) && !next.equals(destination)) || distance.containsKey(next)) continue;
                distance.put(next, distance.get(current) + 1);
                queue.addLast(next);
            }
        }
        return -1;
    }

    private List<BoardPosition> neighbors(BoardPosition position) {
        List<BoardPosition> result = new ArrayList<>();
        for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            int x = position.x() + dx;
            int y = position.y() + dy;
            if (x >= 0 && x < BoardPosition.WIDTH && y >= 0 && y < BoardPosition.HEIGHT) {
                result.add(new BoardPosition(x, y));
            }
        }
        return result;
    }
}
