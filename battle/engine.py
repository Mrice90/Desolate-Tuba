from characters import Character
from cards import Card
from effects.status_effects import DamageOverTime
from enemy_ai import take_turn


def simple_damage(user, target, amount):
    target.hp -= amount
    print(f"{user.name} deals {amount} damage to {target.name}!")


def simple_heal(user, target, amount):
    user.hp = min(user.max_hp, user.hp + amount)
    print(f"{user.name} heals {amount} HP!")


def create_basic_cards():
    return [
        Card("Firebolt", cost=2, resource_type="mana", effect_function=lambda u, t: simple_damage(u, t, 5), description="Deal 5 damage."),
        Card("Meditate", cost=1, resource_type="mana", effect_function=lambda u, t: simple_heal(u, t, 3), description="Heal 3 HP."),
        Card("Strike", cost=2, resource_type="stamina", effect_function=lambda u, t: simple_damage(u, t, 4), description="Physical attack."),
        Card("Focus", cost=1, resource_type="stamina", effect_function=lambda u, t: simple_heal(u, t, 2), description="Recover 2 HP."),
        Card(
            "Burn", cost=2, resource_type="mana",
            effect_function=lambda u, t: t.add_effect(DamageOverTime("Burn", 3, 2)),
            description="Deal 2 damage each turn for 3 turns."
        ),
    ]


def run_battle(player: Character, enemy: Character):
    player.refill_hand()
    enemy.refill_hand()
    turn = 0
    while not player.is_defeated() and not enemy.is_defeated():
        player.regenerate()
        enemy.regenerate()
        player.update_effects()
        enemy.update_effects()
        turn += 1
        print(f"\n-- Turn {turn} --")
        print(f"{player.name} HP:{player.hp} Mana:{player.mana} Stamina:{player.stamina}")
        print(f"{enemy.name} HP:{enemy.hp} Mana:{enemy.mana} Stamina:{enemy.stamina}")
        print("Your hand:")
        for idx, card in enumerate(player.hand):
            print(f"{idx+1}. {card.name} - Cost {card.cost} {card.resource_type} :: {card.description}")
        choice = input("Play a card (1-4) or 'q' to quit: ")
        if choice.lower() == 'q':
            print("You fled the duel!")
            return
        try:
            card_index = int(choice) - 1
        except ValueError:
            print("Invalid choice")
            continue
        if not player.play_card(card_index, enemy):
            print("Failed to play card!")
            continue
        if enemy.is_defeated():
            break
        take_turn(enemy, player)
    if player.is_defeated():
        print("You lost the battle!")
    else:
        print("You won the battle!")

