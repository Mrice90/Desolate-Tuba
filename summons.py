from dataclasses import dataclass
from typing import Optional




@dataclass
class Summon:
    """Simple summoned creature that acts each turn."""

    name: str
    damage: int
    duration: int
    color: str = "gray"

    def act(self, owner, target):
        """Apply this summon’s effect to the target."""
        if self.damage > 0:
            bonus = getattr(owner, "stat_mod", lambda s: 0)("thaumaturgy")
            target.take_damage(max(0, self.damage + bonus))
        self.duration -= 1

    def expired(self) -> bool:
        return self.duration <= 0
