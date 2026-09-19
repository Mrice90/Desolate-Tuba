# Card data format

Infinite Conquest card balance is externalized as versioned JSON. The current schema version is `1`.

## Design rules

- `id` is a permanent lowercase snake_case identity. Never change it when rebalancing a card.
- `contentStatus` is `PROTOTYPE`, `PLAYTEST`, or `APPROVED`.
- `keywords` contains typed engine identifiers. Schema v1 recognizes `MOLE`, `VANGUARD`, and `BLINK`.
- `rulesText` preserves human-readable ability wording. It does not execute code.
- `faction` may be `UNASSIGNED` while a prototype has no confirmed faction.
- Lands, Structures, and Capitals must have positive `hitPoints`; other card types use zero unless a later schema changes that rule.
- Catalog loading rejects unknown schema versions, duplicate IDs, malformed IDs, negative stats, and invalid enum values.

Executable keyword behavior is registered in Java through `KeywordEffectRegistry`. This keeps JSON data-only and prevents card files from executing arbitrary logic.

## Prototype source

`game-core/src/main/resources/cards/prototype-characters.json` contains the five current Character rows imported from the Drive worksheet. These are editable test cards, not locked production balance. The Capital, Structure, Spell, and Land worksheets contain headers only, so no placeholder cards were invented.
