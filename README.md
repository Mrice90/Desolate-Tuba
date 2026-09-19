# Infinite Conquest

Infinite Conquest is a tactical card game combining deck construction, a shared 4×6 battlefield, spatial combat, stacking, and faction-driven strategies.

This repository was rebuilt from the former Medieval Duel prototype. The original project remains recoverable through Git history. The separate `Mrice90/Creepy-Tomatoe` Ninja vs Zombies repository is not touched by this work.

## Play the graphical prototype

Requires JDK 17, Gradle 8+, and a desktop environment.

```bash
gradle :game-gui:run
```

The graphical client uses the same tested engine as the CLI. Choose both factions and one of three Capitals per side before the match, with strategy and passive summaries shown in setup. Select a hand card or battlefield cell to filter its legal actions, then double-click an action or press **Execute Selected**. It includes the full 4×6 battlefield, real faction starter decks, card faces, stacks, GP and deck meters, automatic bot turns, reaction windows, match results, and faction-colored placeholder styling ready for later artwork.

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

The editor starts with the demo deck. It supports 264 editable prototypes: 240 faction cards across regular, apex, and keyword tiers, plus 24 neutral/development cards. The 18 Capitals are selected separately and never count toward the 40-card deck.

```bash
gradle :game-cli:run --args="deck"
```

Use `factions`, `pool <faction>`, or `reset <faction>` to explore a starter. Use `swap <remove-id> <add-id>`, then `save my-deck.json`. A deck saves only when it contains exactly 40 cards and no card has more than four copies.

Play using a saved human deck against a saved bot deck:

```bash
gradle :game-cli:run --args="play human.json bot.json 42"
```

List the three Capital choices for every faction, then optionally select one for each player:

```bash
gradle :game-cli:run --args="capitals"
gradle :game-cli:run --args="play human.json bot.json 42 zeus_capital_keraunos_spire ares_capital_red_citadel"
```

When a deck contains cards from exactly one faction, an omitted Capital defaults to that faction's first choice. A supplied Capital must match a single-faction deck.

All imported and development cards remain editable prototype content rather than locked production balance.

## Build and test

```bash
gradle test
```

## Run automated balance simulations

Run deterministic bot-versus-bot matches across all 36 ordered faction matchups and all nine Capital pairings. The default two repetitions per pairing produce 648 matches:

```bash
gradle :game-cli:run --args="simulate"
```

Choose repetitions, seed, and JSON report path:

```bash
gradle :game-cli:run --args="simulate 10 42 reports/balance.json"
```

Ten repetitions produce 3,240 matches. Reports include faction and Capital win rates, first-player advantage, match length, unused GP, ending hand size, exhaustion frequency, passive activations, card play rates, and automatic balance flags.

## Current capabilities

- desktop graphical client with selectable cards, battlefield cells, legal-action filtering, bot animation, and reaction prompts
- deterministic Player 1 vs Bot matches with automated bot turns and reactions
- deterministic headless bot-versus-bot balance simulations and JSON telemetry
- deterministic setup, hands, draws, GP, phases, and events
- 4×6 battlefield with two 4×3 player plots and ordered stacks
- movement, range, Capitals, deployment, combat, HP, destruction, and victory
- Mole, Blink, Vanguard, line of sight, and typed Spell effects
- private local-player handoff, inspection, and legal-action hints
- six 40-card faction pools with primary/secondary type and keyword identities, plus 24 neutral/development prototypes
- three separately selectable Capitals per faction, each with a unique implemented passive ability
- executable active-turn and enemy-turn reaction Spells
- validated JSON deck files and interactive deck editor
- automated JUnit rules and interface tests
