class Card:
    def __init__(self, name, cost, resource_type, effect_name, description=""):
        self.name = name
        self.cost = cost
        self.resource_type = resource_type
        self.effect_name = effect_name
        self.description = description


class Character:
    def __init__(self, name, hp, mana, stamina,
                 deck=None, hand=None, discard_pile=None, items=None):
        self.name = name
        self.hp = hp
        self.mana = mana
        self.stamina = stamina

        self.deck = deck or []
        self.hand = hand or []
        self.discard_pile = discard_pile or []
        self.items = items or []
