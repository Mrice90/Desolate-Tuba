# Faction Keyword Expansion

This prototype expansion uses only the three rules keywords already implemented by the engine: **Blink**, **Mole**, and **Vanguard**. It adds five Characters to each faction: three primary-keyword cards at low, mid, and high cost (2/5/8 GP), and two secondary-keyword cards at low and mid cost (3/6 GP).

| Faction | Primary keyword | Strategy | Secondary keyword | Strategy |
|---|---|---|---|---|
| Zeus | Blink | Storm mobility and changing attack angles | Vanguard | Protect ranged storm formations |
| Poseidon | Mole | Threats hidden beneath resilient Lands | Vanguard | Build protected tidal positions |
| Hades | Mole | Concealed attrition and delayed ambushes | Blink | Hunt weakened targets through the veil |
| Ares | Vanguard | Aggressive front-line pressure | Blink | Maintain attacks from open flanks |
| Athena | Vanguard | Defensive formations and sight-line control | Mole | Concealed reserves beneath key Lands |
| Hephaestus | Vanguard | Machine screens for Structures and engines | Mole | Counterattack from beneath Forge Lands |

All 30 cards are `PROTOTYPE` content. Their costs and statistics are test values and can be rebalanced without changing the keyword rules.

## Capital selection

Each faction has three selectable Capitals (18 total). Capitals are loaded from a separate catalog, do not count toward the 40-card deck, and are deployed simultaneously using the existing Capital placement rules. The CLI lists them with `capitals` and accepts Capital IDs after the optional seed in `play` mode.

The initial Capital choices share 20 HP so choosing a name does not create a hidden balance advantage. Distinct Capital abilities can be added in a later balance phase once Capital passive rules are defined.
