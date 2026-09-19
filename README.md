# Infinite Conquest

Infinite Conquest is a tactical card game combining deck construction, a 4×3 battlefield, spatial combat, stacking, and faction-driven strategies.

This repository is being rebuilt from the former Medieval Duel prototype. The original project remains recoverable through Git history. The separate `Mrice90/Creepy-Tomatoe` Ninja vs Zombies repository is not touched by this work.

## Current milestone

M1 foundation currently provides:

- pure Java `game-core` module independent of Android
- deterministic match seed and explicit phase state
- authoritative 4×3 board with ordered stacks
- stable card-definition and card-instance identifiers
- 40-card deck validation (maximum four copies, minimum ten distinct definitions)
- GP progression capped at 10
- legal-action validation for playing lands and ending turns
- automated JUnit rule tests
- GitHub Actions build and test workflow

This is an engine foundation, not yet a complete playable game.

## Build and test

Requires JDK 17 and Gradle 8+.

```bash
gradle test
```

## Direction

The engine remains UI-independent so it can later power Android, desktop tools, AI simulations, replays, and potential multiplayer. See `docs/roadmap.md` and `docs/game-rules-digital-spec.md`.
