# Infinite Conquest digital rules specification

This implementation follows the Drive rules and Matt's later decisions. Drive card sheets are prototype balance content, not a locked production set.

## Battlefield and setup

Each player owns a 4×3 plot within the combined 4×6 battlefield. Lands and secret Capitals deploy on their owner's plot. Capitals are committed privately and revealed simultaneously. Opening hands contain five cards.

## Movement and range

Diagonal and orthogonal steps each cost one. Ordinary movement finds paths through empty cells and may be split across multiple actions before or after an attack. Only the top Character of a stack can move or attack normally.

## Placement and stacking

- Lands require an empty cell on their owner's plot.
- Structures require a controlled Land currently on top of the destination stack.
- Characters may be summoned on a friendly Permanent or into an empty cell one diagonal/orthogonal space from one.
- On-Structure summoning currently places the Character on top. Beneath-Structure insertion awaits an exact depth decision.

## Combat

A Character attacks once per turn and targets the top enemy card within its diagonal-inclusive range.

- Against a Character: destroy it only when Attack is strictly greater than Defense. Equal values do not destroy.
- Against a Permanent: add damage equal to Attack. Destroy it when accumulated damage reaches its printed HP.
- Capitals use 20 HP according to the prototype Capital sheet.
- Land and Structure HP are data values and may be tuned.

Destroyed cards move to their owner's discard pile.

## Victory

Lands, Structures and Capitals are Permanents. Immediately after a player loses a Permanent, if they control no remaining Permanents, the opponent wins and the phase becomes Game Over.

## Content status

Prototype spreadsheet cards may be changed, rebalanced, removed or expanded. Stable software IDs must remain separate from names and balance values.
