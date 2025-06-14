import random
from dataclasses import dataclass, field
from typing import List, Optional, Dict

from cards import Card
from effects.status_effects import StatusEffect
from summons import Summon


@dataclass
class Character:
    """A playable or enemy character in the STAR system."""

    name: str
    hp: int
    mana: int
    stamina: int
    deck: Optional[List[Card]] = None
    items: Optional[List] = None
    level: int = 1
    xp: int = 0
    xp_to_next: int = 100
    hp_regen: float = 1
    mana_regen: float = 1
    stamina_regen: float = 1
    progression: Optional[Dict] = None
    strength_mod: int = 0
    thaumaturgy_mod: int = 0
    agility_mod: int = 0
    resilience_mod: int = 0
    armor: int = 0

    # runtime fields -----------------------------------------------------
    max_hp: int = field(init=False)
    max_mana: int = field(init=False)
    max_stamina: int = field(init=False)
    hand: List[Card] = field(init=False, default_factory=list)
    discard_pile: List[Card] = field(init=False, default_factory=list)
    effects: List[StatusEffect] = field(init=False, default_factory=list)
    dodge_chance: int = field(init=False, default=0)
    summons: List[Summon] = field(init=False, default_factory=list)

    def __post_init__(self):
        self.max_hp = self.hp
        self.max_mana = self.mana
        self.max_stamina = self.stamina
        self.progression = self.progression or {}
        self.deck = self.deck[:] if self.deck else []
        self.items = self.items or []

    # --- Stat helpers ----------------------------------------------------
    def stat_mod(self, stat: str) -> int:
        """Return the modifier for one of the STAR stats."""
        stat = stat.lower()
        if stat.startswith("str"):
            return self.strength_mod
        if stat.startswith("tha"):
            return self.thaumaturgy_mod
        if stat.startswith("agi"):
            return self.agility_mod
        if stat.startswith("res"):
            return self.resilience_mod
        return 0

    @property
    def guard(self) -> int:
        """Basic defense rating based on resilience and armor."""
        return 10 + self.resilience_mod + self.armor

    @property
    def speed(self) -> int:
        """Return base speed derived from Agility."""
        return max(1, 5 + self.agility_mod)

    def draw_card(self):
        if not self.deck:
            self.deck, self.discard_pile = self.discard_pile, []
            random.shuffle(self.deck)
        if self.deck:
            card = self.deck.pop(0)
            self.hand.append(card)

    def refill_hand(self):
        while len(self.hand) < 4:
            self.draw_card()

    def play_card(self, index, target):
        if index < 0 or index >= len(self.hand):
            return False
        card = self.hand[index]
        # Level/stat requirements
        if self.level < getattr(card, "level_requirement", 1):
            return False
        for stat, req in getattr(card, "stat_requirements", {}).items():
            if self.stat_mod(stat) < req:
                return False
        if card.resource_type == 'mana' and self.mana >= card.cost:
            self.mana -= card.cost
        elif card.resource_type == 'stamina' and self.stamina >= card.cost:
            self.stamina -= card.cost
        else:
            return False
        # Counter spell check
        if target and card.resource_type == 'mana' and target.has_effect("Counter Spell"):
            target.remove_effect("Counter Spell")
            print(f"{target.name} negates {self.name}'s spell!")
        else:
            card.effect_function(self, target)
        self.discard_pile.append(card)
        self.hand.pop(index)
        self.draw_card()
        return True

    def take_damage(self, amount: int):
        """Reduce HP by ``amount`` without dropping below zero."""
        self.hp = max(0, self.hp - amount)

    def heal(self, amount: int):
        """Increase HP by ``amount`` but do not exceed ``max_hp``."""
        self.hp = min(self.max_hp, self.hp + amount)

    def is_defeated(self):
        return self.hp <= 0

    # --- New convenience methods ---
    def discard_hand(self):
        """Move all cards in hand to the discard pile and refill."""
        self.discard_pile.extend(self.hand)
        self.hand = []
        self.refill_hand()

    # --- Status effect helpers --------------------------------------------
    def add_effect(self, effect: StatusEffect):
        """Add a new status effect to the character."""
        effect.on_apply(self)
        self.effects.append(effect)

    def has_effect(self, name: str) -> bool:
        """Return True if an active effect with ``name`` exists."""
        return any(e.name == name for e in self.effects)

    def remove_effect(self, name: str) -> bool:
        """Remove the first effect with ``name`` if present."""
        for e in list(self.effects):
            if e.name == name:
                e.on_expire(self)
                self.effects.remove(e)
                return True
        return False

    def update_effects(self):
        """Advance all active status effects by one turn."""
        expired = []
        for eff in list(self.effects):
            if eff.tick(self):
                expired.append(eff)
        for eff in expired:
            self.effects.remove(eff)

    def add_summon(self, summon: Summon):
        """Add a summon under this character's control."""
        self.summons.append(summon)

    def update_summons(self, opponent):
        expired = []
        for sm in self.summons:
            sm.act(self, opponent)
            if sm.expired():
                expired.append(sm)
        for sm in expired:
            self.summons.remove(sm)

    def use_item(self, index, target=None):
        if index < 0 or index >= len(self.items):
            return False
        item = self.items.pop(index)
        item.effect_function(self, target)
        return True

    def regenerate(self):
        self.hp = min(self.max_hp, self.hp + self.hp_regen)
        self.mana = min(self.max_mana, self.mana + self.mana_regen)
        self.stamina = min(self.max_stamina, self.stamina + self.stamina_regen)

    def gain_xp(self, amount):
        self.xp += amount
        while self.xp >= self.xp_to_next:
            self.xp -= self.xp_to_next
            self.level_up()

    def level_up(self):
        self.level += 1
        self.max_hp += self.progression.get('hp_per_level', 2)
        self.max_mana += self.progression.get('mana_per_level', 1)
        self.max_stamina += self.progression.get('stamina_per_level', 1)
        self.hp_regen += self.progression.get('hp_regen_per_level', 0.1)
        self.mana_regen += self.progression.get('mana_regen_per_level', 0.1)
        self.stamina_regen += self.progression.get('stamina_regen_per_level', 0.1)
        # Restore stats on level up
        self.hp = self.max_hp
        self.mana = self.max_mana
        self.stamina = self.max_stamina
