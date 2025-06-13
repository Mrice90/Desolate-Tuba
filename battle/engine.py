from characters import Character
from cards import Card
from effects.status_effects import (
    DamageOverTime,
    StatBuff,
    DodgeBuff,
    CounterSpell,
)
from enemy_ai import take_turn


def simple_damage(user, target, amount, stat="strength"):
    """Deal ``amount`` damage from ``user`` to ``target`` using STAR mods."""
    bonus = 0
    if hasattr(user, "stat_mod"):
        bonus = user.stat_mod(stat)
    scaled = max(0, amount + bonus)
    # Dodge check
    import random
    if getattr(target, "dodge_chance", 0) > 0 and random.randint(1, 100) <= target.dodge_chance:
        print(f"{target.name} dodges the attack!")
        return
    target.take_damage(scaled)
    print(f"{user.name} deals {scaled} damage to {target.name}!")


def simple_heal(user, target, amount):
    bonus = 0
    if hasattr(user, "stat_mod"):
        bonus = user.stat_mod("resilience")
    scaled = max(0, amount + bonus)
    user.hp = min(user.max_hp, user.hp + scaled)
    print(f"{user.name} heals {scaled} HP!")


def gain_resource(user, resource: str, amount: int):
    bonus = 0
    if resource == "mana":
        bonus = getattr(user, "stat_mod", lambda x: 0)("thaumaturgy")
        user.mana = min(user.max_mana, user.mana + amount + bonus)
    elif resource == "stamina":
        bonus = getattr(user, "stat_mod", lambda x: 0)("resilience")
        user.stamina = min(user.max_stamina, user.stamina + amount + bonus)
    print(f"{user.name} gains {amount + bonus} {resource}!")


def create_basic_cards():
    return [
        Card("Strike", 1, "stamina", lambda u, t: simple_damage(u, t, 3), "Deal 3 physical damage."),
        Card("Defend", 1, "stamina", lambda u, t: u.add_effect(StatBuff("Defend", 1, "armor", 4)), "Gain 4 defense until next turn."),
        Card("Meditate", 2, "mana", lambda u, t: gain_resource(u, "mana", 3), "Regain 3 Mana."),
        Card("Dash", 1, "stamina", lambda u, t: u.add_effect(DodgeBuff("Dash", 2, 20)), "Increase dodge chance by 20% for 2 turns."),
        Card("Focus Blast", 2, "mana", lambda u, t: simple_damage(u, t, 5, "thaumaturgy"), "Deal 5 magic damage."),
        Card("Quick Draw", 1, "stamina", lambda u, t: (u.draw_card(), u.draw_card()), "Draw 2 cards."),
        Card("Power Surge", 2, "mana", lambda u, t: u.add_effect(StatBuff("Power Surge", 2, "strength_mod", 2)), "Increase Strength by 2 for 2 turns."),
        Card("Second Wind", 2, "stamina", lambda u, t: simple_heal(u, u, 5), "Recover 5 HP."),
        Card("Counter Spell", 3, "mana", lambda u, t: u.add_effect(CounterSpell()), "Negate the next enemy spell."),
        Card("Inspire", 2, "mana", lambda u, t: (u.add_effect(StatBuff("InspireSTR", 2, "strength_mod", 1)), u.add_effect(StatBuff("InspireAGI", 2, "agility_mod", 1))), "Boost Agility and Strength by 1 for 2 turns."),
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

