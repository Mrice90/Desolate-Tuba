# Infinite Conquest card art system

`CardArtFactory` uses a hybrid art pipeline. Completed rollout categories load their own painterly illustrations, while cards still awaiting bespoke art use the deterministic runtime compositor. The same stable card ID always produces the same visual at every supported display size.

The Character art rollout now covers all 24 Zeus Characters. The first six-card benchmark established mobility, defense, spellcasting, ranged combat, creatures, and apex scale; the second 12-card batch completed the low- and mid-cost roster; and the final six-card batch completed the high-cost roster from Thunderhead Guardian through Aetherbolt Avatar. Characters in factions awaiting their rollout continue to use the procedural compositor.

The Spell art rollout now covers all 10 Zeus Spells: five core tactical effects and their five apex-scale counterparts. Zeus Lands are the next scheduled category, followed by Zeus Structures.

## Painted Capitals

All 18 Capitals have individual 3:2 landscape illustrations in `game-gui/src/main/resources/art/capitals`. They emphasize a strong central architectural silhouette so each Capital remains recognizable in the hand, inspector, and compact board tile. The renderer center-crops and bicubic-scales the source without stretching it.

The six faction families deliberately use different visual languages: Zeus is celestial and storm-crowned; Poseidon is oceanic and monumental; Hades is underworld architecture; Ares is aggressive military geometry; Athena is luminous strategic classicism; and Hephaestus is volcanic machinery.

Painted Characters and Spells live in `game-gui/src/main/resources/art/characters` and `game-gui/src/main/resources/art/spells`, using the same stable card-ID filenames as the Capital system. Rollout order is systematic: finish every Character for a faction, then that faction's Spells, Lands, and Structures before moving to the next faction.

## Visual layers

1. An original six-panel environment atlas establishes each faction's world.
2. Card type selects the central silhouette: Character, Land, Structure, Spell, or Capital.
3. The card name and ID select up to three semantic motifs. Current motifs include lightning, waves, souls, fire, gears, spears, eyes, stars, gates, blades, wings, leaves, hammers, books, and crowns.
4. Stable seeded placement, atmosphere, weapon, tower count, and background crop distinguish cards that share a motif.
5. Ability cards receive a cyan ability marker, while the lower-left monogram reinforces the card's individual identity at board-thumbnail size.
6. CC0 particle textures add faction-specific flame, smoke, sparks, rings, magical orbits, and light blooms without replacing the card's unique composition.

The renderer has a procedural fallback, so a missing or unreadable painted image never makes a Capital invisible.

## Adding cards

New cards require no separate image-file entry. Give each card a descriptive, unique name and stable ID. Words that match existing motifs receive appropriate symbols automatically; unmatched names still receive type-specific art and a unique seeded composition.

When adding a recurring concept, add its vocabulary to `CardArtFactory.find` and either reuse an existing motif or add a new one to the `Motif` enum and `drawMotif`.

## Asset provenance

`game-gui/src/main/resources/art/faction-environments.png`, the 18 files in `game-gui/src/main/resources/art/capitals`, and the painted Character and Spell files are original project artwork generated for Infinite Conquest with OpenAI image generation on September 20, 2026. Each image used a bespoke prompt describing its named subject, faction palette, and strategic identity, with explicit exclusions for text, logos, borders, and watermarks. They contain no downloaded third-party material or licensed characters. The Java compositor and all vector overlays are original project code.

The finishing particle textures come from Kenney's CC0 Particle Pack. Full provenance and the included license are recorded in [`THIRD_PARTY_ASSETS.md`](../THIRD_PARTY_ASSETS.md).
