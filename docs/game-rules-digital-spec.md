# Infinite Conquest digital rules specification

Status: initial implementation draft. The complete approved rulebook remains authoritative. Ambiguities are tracked in `rules-questions.md`.

## Battlefield

- Grid width: 4 columns.
- Grid height: 3 rows.
- Coordinates are zero-based: x 0–3 and y 0–2.
- Every cell owns an ordered stack of card-instance IDs.
- Index 0 is the bottom; the final element is the interactable top unless an effect says otherwise.

## Decks

- Constructed deck size: exactly 40 cards.
- Copy limit: at most four instances of one card-definition ID.
- Diversity: at least ten distinct card-definition IDs.
- Validation belongs to the engine.

## Cards

A `CardDefinition` is immutable content identified by a stable ID. A `CardInstance` is a match-specific copy with its own UUID, owner, zone, damage, and tapped state.

Initial types: Character, Land, Structure, Spell, Capital.

## Turn and GP

Phases are Start, Play, End and Game Over. GP capacity increases once at the start of a player's turn, never exceeding 10, and current GP refills to capacity. Opening values and second-player compensation remain unresolved pending authoritative rule wording.

## Initial implemented actions

- Play a Land from hand into an empty cell during the active player's Play phase.
- End the active player's turn.

All other actions remain explicitly unsupported rather than being approximated.

## Victory

The intended victory condition is destruction of all opposing battlefield permanents. Exact setup and simultaneous-destruction behavior must be confirmed before implementation.
