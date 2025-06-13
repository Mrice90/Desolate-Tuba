import random

class Card:
    def __init__(self, name, cost, resource_type, effect_name, description=""):
        self.name = name
        self.cost = cost
        self.resource_type = resource_type
        self.effect_name = effect_name
        self.description = description


class Character:
    def __init__(self, name, hp, mana, stamina,
                 deck=None, hand=None, discard_pile=None, items=None,
                 strength_mod=0, thaumaturgy_mod=0,
                 agility_mod=0, resilience_mod=0, armor=0):
        self.name = name
        self.max_hp = hp
        self.max_mana = mana
        self.max_stamina = stamina

        self.hp = hp
        self.mana = mana
        self.stamina = stamina

        self.deck = deck or []
        self.hand = hand or []
        self.discard_pile = discard_pile or []
        self.items = items or []
        self.strength_mod = strength_mod
        self.thaumaturgy_mod = thaumaturgy_mod
        self.agility_mod = agility_mod
        self.resilience_mod = resilience_mod
        self.armor = armor

    def stat_mod(self, stat):
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
    def guard(self):
        return 10 + self.resilience_mod + self.armor

    def draw_card(self):
        if not self.deck:
            self.deck, self.discard_pile = self.discard_pile, []
            random.shuffle(self.deck)
        if self.deck:
            self.hand.append(self.deck.pop(0))

    def refill_hand(self):
        while len(self.hand) < 4:
            self.draw_card()

    def play_card(self, index):
        if index < 0 or index >= len(self.hand):
            return False
        card = self.hand[index]
        if card.resource_type == "mana" and self.mana >= card.cost:
            self.mana -= card.cost
        elif card.resource_type == "stamina" and self.stamina >= card.cost:
            self.stamina -= card.cost
        else:
            return False
        self.discard_pile.append(card)
        self.hand.pop(index)
        return True

    def take_damage(self, amount):
        self.hp = max(0, self.hp - amount)

    def heal(self, amount):
        self.hp = min(self.max_hp, self.hp + amount)
