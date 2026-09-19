package com.infiniteconquest.core;

public record MatchRules(int initialHandSize, int maximumGp, int gpGrowthPerPersonalTurn,
        int secondPlayerOpeningGp, int secondPlayerOpeningGpTurns, int cardsDrawnAtTurnStart) {
    public MatchRules {
        if (initialHandSize < 0) throw new IllegalArgumentException("Initial hand size cannot be negative");
        if (maximumGp < 1) throw new IllegalArgumentException("Maximum GP must be positive");
        if (gpGrowthPerPersonalTurn < 0 || secondPlayerOpeningGp < 0
                || secondPlayerOpeningGpTurns < 0 || cardsDrawnAtTurnStart < 0) {
            throw new IllegalArgumentException("Rule values cannot be negative");
        }
    }
    public static MatchRules current() { return new MatchRules(5, 10, 1, 3, 2, 1); }
    public int gpForTurn(int playerId, int personalTurnNumber) {
        if (personalTurnNumber < 1) throw new IllegalArgumentException("Personal turn number starts at 1");
        if (playerId == 1 && personalTurnNumber <= secondPlayerOpeningGpTurns) {
            return Math.min(maximumGp, secondPlayerOpeningGp);
        }
        return Math.min(maximumGp, personalTurnNumber * gpGrowthPerPersonalTurn);
    }
}
