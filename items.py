class Item:
    def __init__(self, name, effect_function, description=""):
        self.name = name
        self.effect_function = effect_function
        self.description = description


def create_basic_items():
    """Return a few simple healing/restoration items."""
    return [
        Item("Health Potion", lambda user, target=None: _heal(user, 5), "Recover 5 HP."),
        Item("Mana Potion", lambda user, target=None: _restore_mana(user, 3), "Restore 3 mana."),
        Item("Stamina Brew", lambda user, target=None: _restore_stamina(user, 3), "Restore 3 stamina."),
    ]


def _heal(character, amount):
    character.hp = min(character.max_hp, character.hp + amount)


def _restore_mana(character, amount):
    character.mana = min(character.max_mana, character.mana + amount)


def _restore_stamina(character, amount):
    character.stamina = min(character.max_stamina, character.stamina + amount)

