# Hex and mobile design study

Open `index.html` in a modern browser from a checkout of this repository, or serve the repository root and visit `/docs/prototypes/hex-mobile/`. Artwork uses relative paths to existing game resources, so keep this folder inside the checkout.

Switch between Stormfront and Obsidian Table, opening and crowded positions, and desktop/phone views. Desktop rotates the same hex topology to place the armies left/right; phone view places them top/bottom. Tap a hex or hand card to inspect it.

The deck screen follows Faction → optional Ally → primary-faction Capital → cards. A valid prototype draft has at least 40 cards, at least 10 distinct IDs, no more than four copies of an ID, and no third faction. `Fill sample deck` is a composition test aid, not a balanced starter. Drafts are browser-local and cannot be imported into the desktop game.

This is an interactive visual study, not a playable game or a phone app. GP, board positions, stacks, and action history are illustrative. Missing unit paintings reuse Capital artwork as a temporary visual placeholder. The sample collection contains the 348 existing JSON-defined cards, excluding 60 tutor cards generated in Java; all 18 Capitals are included. No storefront, faction locks, or real purchases are implemented.

See [the rules, mobile, alliance, and business proposal](../../hex-mobile-and-allies-proposal.md) for confirmed decisions, proposals, and implementation order.

## Verify

With Node and Playwright available, run `node docs/prototypes/hex-mobile/verify.cjs` from the repository root. Optionally set `IC_BROWSER_EXECUTABLE` to a local Chromium-based browser executable. The script checks geometry, deck composition, inspection, saving, desktop height, image loading, and phone overflow/touch bounds. It writes seven preview images to `build/hex-prototype/`.

Passing browser checks do not establish physical-device playability, native-store readiness, or game balance. Existing game rules and the desktop build are unchanged by these files.
