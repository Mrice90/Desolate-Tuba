"""Simple enemy AI utilities.

This module provides helper functions for selecting and playing cards for
enemy characters during a battle. The logic here is intentionally simple but
gives enemies a semblance of decision making.
"""

from typing import Optional

from characters import Character


def choose_card_index(enemy: Character) -> Optional[int]:
    """Select a playable card index from ``enemy.hand``.

    The AI looks for the highest cost card it can afford based on the
    card's resource type. If no cards are playable, ``None`` is returned.
    """
    playable_indexes = []
    for idx, card in enumerate(enemy.hand):
        if card.resource_type == "mana" and enemy.mana >= card.cost:
            playable_indexes.append(idx)
        elif card.resource_type == "stamina" and enemy.stamina >= card.cost:
            playable_indexes.append(idx)
    if not playable_indexes:
        return None
    # Prefer the highest cost card for a slightly more threatening AI
    return max(playable_indexes, key=lambda i: enemy.hand[i].cost)


def take_turn(enemy: Character, player: Character) -> None:
    """Execute the enemy's turn against ``player``."""
    idx = choose_card_index(enemy)
    if idx is not None:
        card = enemy.hand[idx]
        enemy.play_card(idx, player)
        print(f"{enemy.name} plays {card.name}!")
    else:
        # If unable to play a card, discard and redraw to try again next round
        enemy.discard_hand()
        print(f"{enemy.name} hesitates and redraws.")
    enemy.refill_hand()
