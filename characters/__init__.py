import random
from cards import Card

class Character:
    def __init__(self, name, hp, mana, stamina, deck=None, items=None):
        self.name = name
        self.max_hp = hp
        self.hp = hp
        self.mana = mana
        self.stamina = stamina
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
