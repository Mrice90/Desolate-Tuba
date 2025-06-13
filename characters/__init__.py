import random
from cards import Card

class Character:
    def __init__(self, name, hp, mana, stamina, deck=None, items=None,
                 level=1, xp=0, xp_to_next=100,
                 hp_regen=1, mana_regen=1, stamina_regen=1):
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

        self.hp_regen = hp_regen
        self.mana_regen = mana_regen
        self.stamina_regen = stamina_regen

        self.deck = deck[:] if deck else []
        self.hand = []
        self.discard_pile = []
        self.items = items or []

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
        card.effect_function(self, target)
        self.discard_pile.append(card)
        self.hand.pop(index)
        self.draw_card()
        return True

    def is_defeated(self):
        return self.hp <= 0

    # --- New convenience methods ---
    def discard_hand(self):
        """Move all cards in hand to the discard pile and refill."""
        self.discard_pile.extend(self.hand)
        self.hand = []
        self.refill_hand()

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
        self.max_hp += 2
        self.max_mana += 1
        self.max_stamina += 1
        self.hp_regen += 1
        self.mana_regen += 1
        self.stamina_regen += 1
        # Restore stats on level up
        self.hp = self.max_hp
        self.mana = self.max_mana
        self.stamina = self.max_stamina
