from dataclasses import dataclass


@dataclass
class StatusEffect:
    """Base class for status effects applied to a Character."""

    name: str
    duration: int

    def on_apply(self, target):
        """Hook called when the effect is first added."""
        pass

    def on_turn(self, target):
        """Hook called each turn while the effect is active."""
        pass

    def on_expire(self, target):
        """Hook called when the effect expires."""
        pass

    def tick(self, target) -> bool:
        """Advance one turn of the effect.

        Returns True if the effect expired this tick.
        """
        self.on_turn(target)
        self.duration -= 1
        if self.duration <= 0:
            self.on_expire(target)
            return True
        return False


@dataclass
class DamageOverTime(StatusEffect):
    """Simple damage over time effect."""

    amount: int

    def on_turn(self, target):
        target.take_damage(self.amount)


@dataclass
class StatBuff(StatusEffect):
    """Temporarily modify a character stat or attribute."""

    attr: str
    amount: int

    def on_apply(self, target):
        setattr(target, self.attr, getattr(target, self.attr, 0) + self.amount)

    def on_expire(self, target):
        setattr(target, self.attr, getattr(target, self.attr, 0) - self.amount)


@dataclass
class DodgeBuff(StatusEffect):
    """Increase dodge chance by a flat amount."""

    chance: int

    def on_apply(self, target):
        target.dodge_chance += self.chance

    def on_expire(self, target):
        target.dodge_chance = max(0, target.dodge_chance - self.chance)


@dataclass
class CounterSpell(StatusEffect):
    """Negate the next mana-based card used against the target."""
    name: str = "Counter Spell"
    duration: int = 1
