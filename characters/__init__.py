import random
from cards import Card
from effects.status_effects import StatusEffect

class Character:
    def __init__(self, name, hp, mana, stamina, deck=None, items=None,
                 level=1, xp=0, xp_to_next=100,
                 hp_regen=1, mana_regen=1, stamina_regen=1,
                 progression=None,
                 strength_mod=0, thaumaturgy_mod=0,
                 agility_mod=0, resilience_mod=0, armor=0):
        self.name = name
        self.level = level
        self.xp = xp
        self.xp_to_next = xp_to_next

        self.max_hp = hp
        self.max_mana = mana
        self.max_stamina = stamina

        self.hp = hp
        self.mana = mana
        self.stamina = stamina

        self.strength_mod = strength_mod
        self.thaumaturgy_mod = thaumaturgy_mod
        self.agility_mod = agility_mod
        self.resilience_mod = resilience_mod
        self.armor = armor

        self.hp_regen = hp_regen
        self.mana_regen = mana_regen
        self.stamina_regen = stamina_regen
        self.progression = progression or {}

        self.deck = deck[:] if deck else []
        self.hand = []
        self.discard_pile = []
        self.items = items or []
        self.effects = []
        # Chance to completely avoid an incoming attack (0-100)
        self.dodge_chance = 0

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
