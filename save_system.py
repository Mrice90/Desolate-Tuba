import json
import os
from typing import Dict

from characters import Character
from deck_builder import _make_card
from character_cards import CHARACTER_CARDS, UNIVERSAL_CARDS, CHARACTER_PROGRESSIONS

_SAVE_PATH = "savegame.json"

# Build a mapping of card name -> card prototype
_CARD_LOOKUP: Dict[str, Character] = {}
for info in UNIVERSAL_CARDS:
    card = _make_card(info)
    _CARD_LOOKUP[card.name] = card
for char in CHARACTER_CARDS.values():
    for info in char["cards"]:
        card = _make_card(info)
        _CARD_LOOKUP[card.name] = card

_DEFAULT_CHARACTER = next(iter(CHARACTER_CARDS))
_DEFAULT_PROGRESSION = CHARACTER_PROGRESSIONS[_DEFAULT_CHARACTER]


def save_game(player: Character, path: str = _SAVE_PATH) -> None:
    """Save ``player`` to ``path``."""
    data = {
        "name": player.name,
        "level": player.level,
        "xp": player.xp,
        "stats": {
            "strength_mod": player.strength_mod,
            "thaumaturgy_mod": player.thaumaturgy_mod,
            "agility_mod": player.agility_mod,
            "resilience_mod": player.resilience_mod,
        },
        "icon": player.icon,
        "deck": [c.name for c in player.deck],
        "unlocked_unique": [c.name for c in player.unlocked_unique_cards],
    }
    with open(path, "w") as f:
        json.dump(data, f)


def load_game(path: str = _SAVE_PATH) -> Character | None:
    """Load and return a ``Character`` from ``path`` if it exists."""
    if not os.path.exists(path):
        return None
    with open(path) as f:
        data = json.load(f)
    prog = CHARACTER_PROGRESSIONS.get(data.get("name"), _DEFAULT_PROGRESSION)
    player = Character(
        name=data.get("name", _DEFAULT_CHARACTER),
        hp=prog["base_hp"],
        mana=prog["base_mana"],
        stamina=prog["base_stamina"],
        progression=prog,
        icon=data.get("icon", "@"),
        items=[],
    )
    player.level = data.get("level", 1)
    player.xp = data.get("xp", 0)
    player.strength_mod = data.get("stats", {}).get("strength_mod", 0)
    player.thaumaturgy_mod = data.get("stats", {}).get("thaumaturgy_mod", 0)
    player.agility_mod = data.get("stats", {}).get("agility_mod", 0)
    player.resilience_mod = data.get("stats", {}).get("resilience_mod", 0)

    # reconstruct deck and unlocks
    player.deck = [_CARD_LOOKUP[name] for name in data.get("deck", []) if name in _CARD_LOOKUP]
    player.unlocked_unique_cards = [_CARD_LOOKUP[name] for name in data.get("unlocked_unique", []) if name in _CARD_LOOKUP]
    return player
