package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.GameState;
import com.infiniteconquest.core.Phase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public final class InfiniteConquestCli {
    private InfiniteConquestCli() {}

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        DemoMatchFactory matches = new DemoMatchFactory();

        if (args.length > 0 && args[0].equalsIgnoreCase("deck")) {
            new DeckEditorCli(matches.pool(), new DeckFileStore())
                    .run(input, System.out, matches.demoDeck());
            return;
        }

        long seed;
        List<CardDefinition> humanDeck;
        List<CardDefinition> botDeck;
        if (args.length > 0 && args[0].equalsIgnoreCase("play")) {
            if (args.length < 3 || args.length > 4) {
                throw new IllegalArgumentException("Use: play <human-deck.json> <bot-deck.json> [seed]");
            }
            DeckFileStore files = new DeckFileStore();
            humanDeck = files.load(Path.of(args[1]), matches.pool());
            botDeck = files.load(Path.of(args[2]), matches.pool());
            seed = args.length == 4 ? parseSeed(args[3]) : 1L;
        } else {
            seed = args.length == 0 ? 1L : parseSeed(args[0]);
            humanDeck = matches.demoDeck();
            botDeck = matches.demoDeck();
        }

        runMatch(matches.create(seed, humanDeck, botDeck), seed, input);
    }

    private static void runMatch(GameState state, long seed, BufferedReader input) throws IOException {
        BattlefieldRenderer renderer = new BattlefieldRenderer();
        CommandProcessor commands = new CommandProcessor(state);
        ActionHints hints = new ActionHints();
        BotPlayer bot = new BotPlayer();

        System.out.println("Infinite Conquest — Player 1 vs Bot");
        System.out.println("You are Player 1. The bot is Player 2.");
        System.out.println("Seed: " + seed);
        System.out.println(CommandProcessor.help());

        while (!commands.quitRequested() && state.phase() != Phase.GAME_OVER) {
            if (state.activePlayer() == BotPlayer.BOT_ID) {
                BotPlayer.Decision decision = bot.takeNextAction(state, commands);
                System.out.println("Bot: " + decision.command() + " — " + decision.result());
                if (!decision.command().equals("end") && state.phase() != Phase.GAME_OVER) {
                    offerHumanReaction(state, input, renderer, commands, hints);
                }
                continue;
            }

            System.out.println();
            System.out.println(renderer.render(state));
            System.out.print("> ");
            String result = commands.execute(input.readLine());
            if (!result.isBlank()) System.out.println(result);
            if (result.startsWith("OK:") && state.activePlayer() == 0 && state.phase() != Phase.GAME_OVER) {
                BotPlayer.Decision reaction = bot.react(state, commands);
                if (reaction != null) {
                    System.out.println("Bot reaction: " + reaction.command() + " — " + reaction.result());
                }
            }
        }

        if (state.phase() == Phase.GAME_OVER) {
            System.out.println();
            System.out.println(renderer.render(state));
            System.out.println(state.winner().orElseThrow() == 0 ? "You win!" : "The bot wins.");
        }
    }

    private static void offerHumanReaction(GameState state, BufferedReader input,
                                           BattlefieldRenderer renderer, CommandProcessor commands,
                                           ActionHints hints) throws IOException {
        List<String> reactions = hints.spellActionsForPlayer(state, 0);
        if (reactions.isEmpty()) return;
        System.out.println();
        System.out.println("Reaction window — saved GP: " + state.player(0).currentGp());
        System.out.println(renderer.renderHand(state, 0));
        System.out.println("Legal reactions:");
        reactions.forEach(action -> System.out.println("  " + action));
        System.out.print("reaction> ");
        String response = input.readLine();
        if (response == null || response.isBlank() || response.equalsIgnoreCase("pass")) {
            System.out.println("Reaction passed.");
            return;
        }
        if (!response.startsWith("react 0 ")) {
            System.out.println("Reaction passed: use one listed 'react 0' command.");
            return;
        }
        System.out.println(commands.execute(response));
    }

    private static long parseSeed(String value) {
        try { return Long.parseLong(value); }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Optional seed must be a whole number", exception);
        }
    }
}
