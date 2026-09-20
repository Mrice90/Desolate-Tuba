# Infinite Conquest — digital rules specification

This document records implemented rules. Prototype card balance remains editable.

## Battlefield and setup

- Two players share a 4×6 battlefield.
- Player 0 controls rows 0–2; player 1 controls rows 3–5.
- Player 0 draws five opening cards; Player 1 draws six as a second-player advantage.
- Capitals are committed secretly and revealed simultaneously.

## Turns and resources

- Before play, each player places their chosen Capital secretly on any cell of their own 4×3 plot.
- A visible coin flip determines the starting player. The starting player opens with 10 GP and five cards; the second player opens with 12 GP and six cards.
- GP is persistent and does not automatically refill or increase by turn number.
- At the start of a player's turn, each Land and Structure they control generates 1 GP, then controlled cards refresh and the player draws one card.
- Lands and Structures cost no GP. Their printed number is a development value: a value-1 card is legal from personal turn 1 onward, a value-3 card from personal turn 3 onward, and so on.
- Characters and Spells retain their printed GP costs.
- Empty-deck draws deal one exhaustion damage to each controlled Permanent.
- Conquest Pressure deals 3 damage to each active player's Permanent at the start of turns 9–14, then 4 damage from turn 15 onward. From turns 9–22, each player receives exactly seven pressure pulses.

## Deployment and stacks

- Lands enter empty spaces on their owner's plot.
- Structures enter on top of a controlled Land.
- Characters may enter on a friendly Permanent or an empty adjacent space, including diagonals.
- A Character with Mole may instead use the Burrow action to enter directly beneath a controlled Land.
- Only the top card of a stack normally moves, attacks, blocks line of sight, or can be targeted.
- Removing a covering Land reveals the Mole beneath it.

## Movement and Blink

- Normal movement uses eight directions; diagonals cost one space.
- Movement may be split across actions up to the Character's movement value.
- Occupied cells block normal movement.
- A top Character with Blink may move to any empty battlefield square once per personal turn.
- Blink costs no normal movement points.

## Combat and line of sight

- Characters attack once per turn and may aim diagonally.
- Range uses diagonal distance.
- Structures, Capitals, and top Characters with Vanguard block line of sight through their cell.
- The attack target does not block its own line of sight.
- Characters are destroyed only when Attack is strictly greater than Defense.
- Permanents accumulate Attack as damage and are destroyed at their HP threshold.

## Victory

- Lands, Structures, and Capitals are Permanents.
- A player loses immediately after their final Permanent is destroyed.
- If neither player has won by the end of turn 22, the player with more surviving Permanents wins. If tied, total remaining Permanent HP breaks the tie; equal HP produces a draw.
