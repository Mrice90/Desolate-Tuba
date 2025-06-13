from dataclasses import dataclass
from typing import Callable


@dataclass
class Card:
    """A single card that can be played by a character."""

    name: str
    cost: int
    resource_type: str
    effect_function: Callable
    description: str = ""
