package com.infiniteconquest.cli;

import com.infiniteconquest.core.CardDefinition;
import com.infiniteconquest.core.CardType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class DeckEditorCli {
    private final PrototypeCardPool pool;
    private final DeckFileStore files;
    private final FactionDecks factionDecks;

    public DeckEditorCli(PrototypeCardPool pool, DeckFileStore files) {
        this.pool = pool;
        this.files = files;
        this.factionDecks = new FactionDecks(pool);
    }

    public void run(BufferedReader input, PrintStream output, List<CardDefinition> startingDeck) throws IOException {
        DeckEditor editor = new DeckEditor(pool, startingDeck);
        output.println("Infinite Conquest deck editor");
        output.println("Starting from the 40-card demo deck. Type help.");

        while (true) {
            output.print("deck> ");
            String line = input.readLine();
            if (line == null) return;
            String[] parts = line.trim().split("\\s+");
            try {
                switch (parts[0].toLowerCase()) {
                    case "help" -> output.println(help());
                    case "factions" -> output.println(String.join(", ", FactionDecks.FACTIONS));
                    case "pool" -> {
                        if (parts.length > 2) throw new IllegalArgumentException("Use pool or pool <faction>");
                        output.println(renderPool(parts.length == 2 ? parts[1] : null));
                    }
                    case "deck" -> output.println(renderDeck(editor));
                    case "reset" -> {
                        require(parts, 2);
                        editor.reset(factionDecks.starter(parts[1]));
                        output.println("Loaded the " + parts[1].toUpperCase() + " 40-card starter deck.");
                    }
                    case "add" -> { require(parts, 2); editor.add(parts[1]); output.println("Added " + parts[1]); }
                    case "remove" -> { require(parts, 2); editor.remove(parts[1]); output.println("Removed " + parts[1]); }
                    case "swap" -> {
                        require(parts, 3); editor.swap(parts[1], parts[2]);
                        output.println("Swapped " + parts[1] + " for " + parts[2]);
                    }
                    case "save" -> {
                        require(parts, 2); files.save(Path.of(parts[1]), "Custom Deck", editor.cards());
                        output.println("Saved valid 40-card deck to " + parts[1]);
                    }
                    case "validate" -> {
                        List<String> errors = editor.validationErrors();
                        output.println(errors.isEmpty() ? "Deck is valid." : String.join(System.lineSeparator(), errors));
                    }
                    case "quit", "exit" -> { return; }
                    case "" -> { }
                    default -> output.println("Unknown command. Type help.");
                }
            } catch (IllegalArgumentException exception) {
                output.println("Cannot complete command: " + exception.getMessage());
            }
        }
    }

    String renderPool(String faction) {
        List<CardDefinition> visible = faction == null ? pool.cards() : pool.cardsForFaction(faction);
        if (visible.isEmpty()) throw new IllegalArgumentException("No cards found for faction: " + faction);
        StringBuilder out = new StringBuilder();
        for (CardDefinition card : visible) {
            out.append(card.id()).append(" | ").append(card.name()).append(" | ")
                    .append(card.faction()).append(" | ").append(card.type())
                    .append(" | ").append(card.type() == CardType.LAND || card.type() == CardType.STRUCTURE
                            ? "Turn " + Math.max(1, card.cost()) + " (free)" : card.cost() + " GP");
            if (!card.keywords().isEmpty()) out.append(" | ").append(card.keywords());
            out.append(System.lineSeparator());
        }
        return out.toString().stripTrailing();
    }

    String renderDeck(DeckEditor editor) {
        StringBuilder out = new StringBuilder("Cards: " + editor.cards().size() + "/40");
        for (Map.Entry<String, Long> entry : editor.counts().entrySet()) {
            out.append(System.lineSeparator()).append(entry.getValue()).append("x ")
                    .append(pool.require(entry.getKey()).name()).append(" [")
                    .append(entry.getKey()).append(']');
        }
        return out.toString();
    }

    private void require(String[] parts, int length) {
        if (parts.length != length) throw new IllegalArgumentException("Wrong number of arguments");
    }

    private String help() {
        return """
                factions                     list the six launch factions
                pool [faction]               list all cards or one faction's cards
                reset <faction>              load that faction's 40-card starter
                deck                         show the current deck and copy counts
                swap <remove-id> <add-id>    replace one card while staying at 40
                remove <card-id>             remove one copy
                add <card-id>                add one copy (maximum four)
                validate                     check exact 40-card and copy-limit rules
                save <file.json>             save only if the deck is valid
                quit                         leave the editor
                """.strip();
    }
}
