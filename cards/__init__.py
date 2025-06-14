from dataclasses import dataclass, field
from typing import Callable, Dict


@dataclass
class Card:
    """A single card that can be played by a character."""

    name: str
    cost: int
    resource_type: str
    effect_function: Callable
    description: str = ""
    card_type: str = ""
    rarity: str = "common"
    level_requirement: int = 1
    stat_requirements: Dict[str, int] = field(default_factory=dict)
    category: str = "universal"
