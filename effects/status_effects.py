class StatusEffect:
    """Base class for status effects applied to a Character."""

    def __init__(self, name: str, duration: int):
        self.name = name
        self.duration = duration

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


class DamageOverTime(StatusEffect):
    """Simple damage over time effect."""

    def __init__(self, name: str, duration: int, amount: int):
        super().__init__(name, duration)
        self.amount = amount

    def on_turn(self, target):
        target.take_damage(self.amount)
