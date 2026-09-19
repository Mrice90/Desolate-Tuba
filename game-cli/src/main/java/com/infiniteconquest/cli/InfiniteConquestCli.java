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
        List<CardDefinition> playerZero;
        List<CardDefinition> playerOne;
        if (args.length > 0 && args[0].equalsIgnoreCase("play")) {
            if (args.length < 3 || args.length > 4) {
                throw new IllegalArgumentException("Use: play <player0-deck.json> <player1-deck.json> [seed]");
            }
            DeckFileStore files = new DeckFileStore();
            playerZero = files.load(Path.of(args[1]), matches.pool());
            playerOne = files.load(Path.of(args[2]), matches.pool());
            seed = args.length == 4 ? parseSeed(args[3]) : 1L;
        } else {
            seed = args.length == 0 ? 1L : parseSeed(args[0]);
            playerZero = matches.demoDeck();
            playerOne = matches.demoDeck();
        }

        runMatch(matches.create(seed, playerZero, playerOne), seed, input);
    }

    private static void runMatch(GameState state, long seed, BufferedReader input) throws IOException {
        BattlefieldRenderer renderer = new BattlefieldRenderer();
        CommandProcessor commands = new CommandProcessor(state);
        TurnHandoff handoff = new TurnHandoff();
        int acknowledgedTurn = -1;

        System.out.println("Infinite Conquest — local two-player prototype");
        System.out.println("Seed: " + seed);
        System.out.println(CommandProcessor.help());

        while (!commands.quitRequested() && state.phase() != Phase.GAME_OVER) {
            if (acknowledgedTurn != state.turnNumber()) {
                handoff.awaitReady(state, input, System.out);
                acknowledgedTurn = state.turnNumber();
            }
            System.out.println();
            System.out.println(renderer.render(state));
            System.out.print("> ");
            String result = commands.execute(input.readLine());
            if (!result.isBlank()) System.out.println(result);
        }

        if (state.phase() == Phase.GAME_OVER) {
            System.out.println();
            System.out.println(renderer.render(state));
            System.out.println("Player " + state.winner().orElseThrow() + " wins!");
        }
    }

    private static long parseSeed(String value) {
        try { return Long.parseLong(value); }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Optional seed must be a whole number", exception);
        }
    }
}
