# Automated balance simulator

The headless simulator lets the deterministic bot control both players and rotates through every ordered faction matchup and every Capital pairing. With six factions and three Capitals per faction, each repetition runs 324 matches. The default is two repetitions (648 matches).

## Run

```bash
gradle :game-cli:run --args="simulate [matches-per-capital-pair] [seed] [report.json]"
```

Examples:

```bash
gradle :game-cli:run --args="simulate"
gradle :game-cli:run --args="simulate 10 42 reports/balance.json"
```

Each match is capped at 120 turns and 200 actions per turn. A capped match is recorded as a draw rather than hanging the simulation. Seeds are derived deterministically from the supplied base seed, so the same content and arguments reproduce the same results.

## Report fields

- completed matches and draws;
- average turn count;
- first-player win rate;
- average unspent GP at turn end;
- average ending hand size;
- matches that reached exhaustion;
- faction games, wins, and win rates;
- Capital games, wins, win rates, and passive triggers per game;
- card deck appearances, plays, play rate, and wins when played;
- automatic flags for faction win rates outside 45–55%, Capital win rates outside 40–60%, rarely triggered passives, and draw rates above 10%.

The simulator also resolves lethal exhaustion damage. This closes matches whose decks are empty instead of allowing Permanents to remain on the battlefield beyond their HP.

## Interpretation

The current bot is a deterministic heuristic opponent, not a perfect player. Results identify likely balance and usability problems, but human playtests remain necessary. A card with a low play rate may be underpowered, too expensive, too situational, or simply undervalued by the bot's present strategy.
