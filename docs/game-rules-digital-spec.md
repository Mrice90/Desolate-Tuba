# Infinite Conquest digital rules specification

Status: implementation draft derived from the Google Drive document **Infinite Conques Rules** and subsequent decisions by Matt.

## Battlefield and setup

- Each player controls a 4×3 deployment plot.
- The two plots join into one 4×6 battlefield.
- Canonical coordinates use columns x=0–3. Player 1 controls y=0–2; Player 2 controls y=3–5.
- A client may rotate the view without changing canonical coordinates.
- Lands may initially be placed only within their owner's plot.
- Each player chooses a Capital position on their plot and places it face-down.
- Capital choices may be changed until both players are ready, then first player is chosen by coin flip.
- Every cell owns an ordered stack; only the top card is normally targetable.

## Decks and deterministic setup

- Constructed deck size: exactly 40 cards.
- Opening hand size: exactly 5 cards.
- Both decks are validated before match state is created.
- A match seed controls shuffle order and card-instance UUIDs.

## Cards and placement

- Characters: pay cost and summon within one square of, or on, a friendly permanent.
- Lands: play on an open space on the player's side.
- Structures: normally play on a controlled Land.
- Capitals: special Structures placed before play.
- Permanents: Lands, Structures and Capitals.

## Turn and GP

Start Phase gains GP, draws one card and untaps controlled cards. Play Phase permits cards, Characters and permanent abilities. End Phase triggers end-of-turn effects.

- GP progresses toward a maximum of 10.
- The second player has exactly 2 GP on both their first and second personal turns.
- Normal progression resumes afterward.

## Combat and stacking

- Movement may be split before and after attacking.
- A Character attacks one Character or permanent in range.
- An attacked Character is destroyed only when Attack is strictly greater than Defense.
- Attacking a permanent reduces HP by Attack; it is destroyed at 0 HP.
- Characters may be placed on a Structure or beneath it.
- Characters cannot be placed beneath a Land without Mole or explicit permission.

## Events and exhaustion

Setup, draws, phase changes, turn transitions and played cards produce sequenced events. When a player cannot draw, each permanent they control takes 1 damage. Destruction waits for permanent HP implementation.

## Victory

A player wins after destroying all opposing permanent structures. Lands and simultaneous destruction remain clarification points.
