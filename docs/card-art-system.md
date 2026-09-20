# Infinite Conquest card art system

Every card is illustrated at runtime by `CardArtFactory`. The illustration is deterministic: the same stable card ID always produces the same composition at every supported display size.

## Visual layers

1. An original six-panel environment atlas establishes each faction's world.
2. Card type selects the central silhouette: Character, Land, Structure, Spell, or Capital.
3. The card name and ID select up to three semantic motifs. Current motifs include lightning, waves, souls, fire, gears, spears, eyes, stars, gates, blades, wings, leaves, hammers, books, and crowns.
4. Stable seeded placement, atmosphere, weapon, tower count, and background crop distinguish cards that share a motif.
5. Ability cards receive a cyan ability marker, while the lower-left monogram reinforces the card's individual identity at board-thumbnail size.
6. CC0 particle textures add faction-specific flame, smoke, sparks, rings, magical orbits, and light blooms without replacing the card's unique composition.

The renderer has a procedural fallback, so a missing image resource never makes cards invisible.

## Adding cards

New cards require no separate image-file entry. Give each card a descriptive, unique name and stable ID. Words that match existing motifs receive appropriate symbols automatically; unmatched names still receive type-specific art and a unique seeded composition.

When adding a recurring concept, add its vocabulary to `CardArtFactory.find` and either reuse an existing motif or add a new one to the `Motif` enum and `drawMotif`.

## Asset provenance

`game-gui/src/main/resources/art/faction-environments.png` is original project artwork generated for Infinite Conquest on September 20, 2026. It contains no downloaded third-party material, logos, or licensed characters. The Java compositor and all vector overlays are original project code.

The finishing particle textures come from Kenney's CC0 Particle Pack. Full provenance and the included license are recorded in [`THIRD_PARTY_ASSETS.md`](../THIRD_PARTY_ASSETS.md).
