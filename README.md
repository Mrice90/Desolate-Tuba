# Infinite Conquest

Infinite Conquest is a tactical card game combining deck construction, a shared 4×6 battlefield, spatial combat, stacking, and faction-driven strategies.

This repository was rebuilt from the former Medieval Duel prototype. The original project remains recoverable through Git history. The separate `Mrice90/Creepy-Tomatoe` Ninja vs Zombies repository is not touched by this work.

## Play the graphical prototype

Requires JDK 17, Gradle 8+, and a desktop environment.

```bash
gradle :game-gui:run
```

The graphical client uses the same tested engine as the CLI. Choose both factions and one of three Capitals per side before the match, with strategy and passive summaries shown in setup.

- Drag a hand card or battlefield unit onto a gold-highlighted legal destination. Click selection plus the **Legal Moves** tab remains available as a keyboard-friendly fallback.
- Hover over any card for a large, readable preview.
- Right-click an occupied battlefield cell to inspect every card in its stack, shown top-first.
- Open the **Action Log** tab to review the match in chronological order.
- Destination labels and colors distinguish movement, ranged or melee attacks, spells, top-of-stack deployment, and Mole burrowing. Ambiguous stack drops ask you to choose the exact action.
- Opening mulligans use two visual card trays, and enemy-turn reactions use a visual spell tray plus battlefield targeting instead of a text menu.
- Choose **Deck Builder** to edit and save one local 40-card deck per faction; saved decks are loaded automatically for new matches.
- The Deck Builder is always available from **Game → Deck Builder** (`Ctrl+D`), even when compact window sizing hides header controls.
- Choose **Bot (watch match)** for Player 1 during setup to run a bot-versus-bot match.
- Battlefield callouts, directional source-to-target animations, and distinct CC0 sound cues identify movement, melee, ranged attacks, spells, destruction, and rule damage. Damaged permanents display both remaining HP and accumulated damage. Audio provenance is documented in `game-gui/src/main/resources/audio/ATTRIBUTION.md`.

The client includes the full 4×6 battlefield, responsive square board tiles with scrolling fallback, pregame Capital placement, an animated graphical initiative coin, unique generated prototype art for every card, real faction starter decks, persistent GP and deck meters, automatic bot turns, reaction windows, and match results.

The initiative winner begins with 10 GP and five cards. The second player begins with 12 GP and six cards. Each player may keep up to three opening cards and replace the rest through the mulligan. Lands and Structures are free once their printed development turn has been reached and show their GP-per-turn output directly. Standard income rises from 1 GP on early development cards to 5 GP on turn-9/10 cards; cards with utility passives generally generate less. There is no automatic per-turn GP and no late-game pressure or turn deadline.

Combat is simultaneous: attack equal to defense destroys a Character, and an in-range defending Character retaliates at the same time. A defender outside its own range cannot retaliate. Moving through an enemy Character's attack range grants that enemy one free opportunity attack per move; human players receive a route warning showing each threat and whether its attack is lethal.

Characters may move onto a friendly Land, Structure, or Capital stack and become its top card. Only the top card of any stack may attack or be attacked. **Fast Strike** prevents retaliation when the attacker strictly exceeds the defender's Defense, **Siege** doubles Character damage to permanents, and **Sharp Shot** grants +1 Attack and +1 Range while its Character is on top of a friendly Structure or Capital.

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

The graphical **Deck Builder** and command-line editor support 330 editable prototypes: 306 faction cards across regular, apex, keyword, tactical, and development tiers, plus 24 neutral/development cards. Each faction now has 51 choices and at least 18 Lands/Structures; its starter uses 18 developments and 22 action cards. The 18 Capitals are selected separately and never count toward the 40-card deck.

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
- Mole, Blink, Vanguard, Fast Strike, Siege, Sharp Shot, line of sight, and typed Spell effects
- private local-player handoff, inspection, and legal-action hints
- six 51-card faction pools with development-heavy 40-card starters, expanded Land/Structure choices, and primary/secondary identities, plus 24 neutral/development prototypes
- three separately selectable Capitals per faction, each with a unique implemented passive ability
- executable active-turn and enemy-turn reaction Spells
- validated JSON deck files and interactive deck editor
- automated JUnit rules and interface tests
