package com.infiniteconquest.cli;

import com.infiniteconquest.core.GameState;
import com.infiniteconquest.core.Phase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class InfiniteConquestCli {
    private InfiniteConquestCli() {}

    public static void main(String[] args) throws IOException {
        long seed = parseSeed(args);
        GameState state = new DemoMatchFactory().create(seed);
        BattlefieldRenderer renderer = new BattlefieldRenderer();
        CommandProcessor commands = new CommandProcessor(state);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Infinite Conquest — local two-player prototype");
        System.out.println("Seed: " + seed);
        System.out.println(CommandProcessor.help());

        while (!commands.quitRequested() && state.phase() != Phase.GAME_OVER) {
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

    private static long parseSeed(String[] args) {
        if (args.length == 0) return 1L;
        try { return Long.parseLong(args[0]); }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Optional seed must be a whole number", exception);
        }
    }
}
