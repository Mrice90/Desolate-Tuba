# Infinite Conquest

Infinite Conquest is a tactical card game combining deck construction, a shared 4×6 battlefield, spatial combat, stacking, and faction-driven strategies.

This repository was rebuilt from the former Medieval Duel prototype. The original project remains recoverable through Git history. The separate `Mrice90/Creepy-Tomatoe` Ninja vs Zombies repository is not touched by this work.

## Play the command-line prototype

Requires JDK 17 and Gradle 8+.

```bash
gradle :game-cli:run
```

Use an optional deterministic seed:

```bash
gradle :game-cli:run --args="42"
```

## Build a custom deck

The editor starts with the demo deck. It supports 234 editable prototypes: 180 faction cards across regular and apex tiers, plus 24 neutral/development cards.

```bash
gradle :game-cli:run --args="deck"
```

Use `factions`, `pool <faction>`, or `reset <faction>` to explore a starter. Use `swap <remove-id> <add-id>`, then `save my-deck.json`. A deck saves only when it contains exactly 40 cards and no card has more than four copies.

Play using a saved human deck against a saved bot deck:

```bash
gradle :game-cli:run --args="play human.json bot.json 42"
```

All imported and development cards remain editable prototype content rather than locked production balance.

## Build and test

```bash
gradle test
```

## Current capabilities

- deterministic Player 1 vs Bot matches with automated bot turns and reactions
- deterministic setup, hands, draws, GP, phases, and events
- 4×6 battlefield with two 4×3 player plots and ordered stacks
- movement, range, Capitals, deployment, combat, HP, destruction, and victory
- Mole, Blink, Vanguard, line of sight, and typed Spell effects
- private local-player handoff, inspection, and legal-action hints
- six 35-card faction pools with primary/secondary apex identities, plus 24 neutral/development prototypes
- executable active-turn and enemy-turn reaction Spells
- validated JSON deck files and interactive deck editor
- automated JUnit rules and interface tests
