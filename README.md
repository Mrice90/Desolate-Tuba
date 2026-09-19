# Infinite Conquest

Infinite Conquest is a tactical card game combining deck construction, a shared 4×6 battlefield, spatial combat, stacking, and faction-driven strategies.

This repository was rebuilt from the former Medieval Duel prototype. The original project remains recoverable through Git history. The separate `Mrice90/Creepy-Tomatoe` Ninja vs Zombies repository is not touched by this work.

## Play the command-line prototype

Requires JDK 17 and Gradle 8+.

```bash
gradle :game-cli:run
```

Use an optional deterministic match seed:

```bash
gradle :game-cli:run --args="42"
```

The local two-player prototype displays the battlefield, active hand, GP, card statistics, and opponent hand count. Type `help` for controls or `actions` to list available command forms for the current state.

The demo uses 40-card decks made from the five imported prototype Characters and clearly labeled development Lands and Structures. These cards exist for testing and remain open to balance and roster changes.

## Build and test

```bash
gradle test
```

## Current capabilities

- deterministic setup, five-card opening hands, draws, GP, phases, and events
- shared 4×6 battlefield with two 4×3 player plots and ordered stacks
- diagonal movement and range
- Capital deployment, Character summoning, Structures, combat, HP, destruction, and victory
- Mole, Blink, Vanguard, and line of sight
- versioned JSON prototype cards with stable IDs
- command-line local two-player matches
- automated JUnit rules and interface tests

The engine remains UI-independent so it can later power Android, AI simulations, replays, and multiplayer. See `docs/roadmap.md` and `docs/game-rules-digital-spec.md`.
