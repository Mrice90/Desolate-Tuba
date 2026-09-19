# Infinite Conquest — digital rules specification

This document records implemented rules. Prototype card balance remains editable.

## Battlefield and setup

- Two players share a 4×6 battlefield.
- Player 0 controls rows 0–2; player 1 controls rows 3–5.
- Player 0 draws five opening cards; Player 1 draws six as a second-player advantage.
- Capitals are committed secretly and revealed simultaneously.

## Turns and resources

- Start Phase refreshes controlled cards, grants GP, and draws one card.
- Normal maximum GP follows 1, 3, 5, 7, 9, 10 across personal turns so mid/high-cost cards enter play sooner.
- Player 1 receives 4 GP on each of their first two personal turns and one extra opening card as prototype second-player advantages, then follows normal GP growth.
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
