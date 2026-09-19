package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;

import java.util.Comparator;
import java.util.List;

public final class BotPlayer {
    public static final int BOT_ID = 1;
    private final ActionHints hints = new ActionHints();

    public Decision takeNextAction(GameState state, CommandProcessor commands) {
        if (state.activePlayer() != BOT_ID) throw new IllegalStateException("It is not the bot's turn");
        List<String> legal = hints.forActivePlayer(state, new GameEngine());
        String command = legal.stream()
                .max(Comparator.comparingInt((String value) -> score(state, value))
                        .thenComparing(Comparator.naturalOrder()))
                .orElse("end");
        return new Decision(command, commands.execute(command));
    }

    public Decision react(GameState state, CommandProcessor commands) {
        if (state.activePlayer() == BOT_ID) return null;
        List<String> legal = hints.spellActionsForPlayer(state, BOT_ID);
        if (legal.isEmpty()) return null;
        String command = legal.stream()
                .max(Comparator.comparingInt((String value) -> score(state, value))
                        .thenComparing(Comparator.naturalOrder()))
                .orElseThrow();
        return new Decision(command, commands.execute(command));
    }

    private int score(GameState state, String command) {
        String[] parts = command.split("\\s+");
        return switch (parts[0]) {
            case "cast", "react" -> spellScore(state, parts);
            case "attack" -> {
                BoardPosition target = new BoardPosition(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                CardInstance card = state.board().topAt(target).flatMap(state::card).orElseThrow();
                yield card.definition().isPermanent() ? 115 : 105;
            }
            case "play" -> {
                int index = Integer.parseInt(parts[1]);
                CardType type = state.card(state.player(BOT_ID).hand().get(index)).orElseThrow().definition().type();
                yield switch (type) {
                    case LAND -> 90;
                    case STRUCTURE -> 85;
                    case CHARACTER -> 80;
                    default -> 0;
                };
            }
            case "burrow" -> 82;
            case "blink" -> 45;
            case "move" -> 35;
            case "end" -> 0;
            default -> 1;
        };
    }

    private int spellScore(GameState state, String[] parts) {
        int handIndex = Integer.parseInt(parts[0].equals("react") ? parts[2] : parts[1]);
        CardInstance spell = state.card(state.player(BOT_ID).hand().get(handIndex)).orElseThrow();
        SpellEffect effect = spell.definition().effects().get(0);
        return switch (effect.type()) {
            case DAMAGE_PERMANENT -> 140 + effect.amount();
            case STRIKE_CHARACTER -> 135 + effect.amount();
            case RETURN_CHARACTER -> 125;
            case HEAL_PERMANENT -> 115 + effect.amount();
            case BUFF_ATTACK -> 105 + effect.amount();
            case BUFF_DEFENSE -> 100 + effect.amount();
            case TELEPORT_CHARACTER -> 60;
        };
    }

    public record Decision(String command, String result) {}
}
