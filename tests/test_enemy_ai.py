import unittest

from characters import Character
from cards import Card
from enemy_ai import choose_card_index, take_turn
from battle.engine import simple_damage


class TestEnemyAI(unittest.TestCase):
    def test_choose_card_index_prefers_high_cost(self):
        high = Card("High", 2, "mana", lambda u, t: simple_damage(u, t, 5))
        low = Card("Low", 1, "mana", lambda u, t: simple_damage(u, t, 3))
        enemy = Character("Enemy", 10, 2, 0, deck=[high, low])
        enemy.hand = [low, high]
        idx = choose_card_index(enemy)
        self.assertEqual(idx, 1)

    def test_take_turn_damages_player(self):
        card = Card("Attack", 2, "mana", lambda u, t: simple_damage(u, t, 4))
        enemy = Character("Enemy", 10, 5, 0, deck=[card]*5)
        enemy.hand = [card]
        player = Character("Hero", 10, 5, 5)
        take_turn(enemy, player)
        self.assertLess(player.hp, 10)

    def test_take_turn_no_play(self):
        card = Card("Attack", 2, "mana", lambda u, t: simple_damage(u, t, 4))
        enemy = Character("Enemy", 10, 1, 0, deck=[card]*5)
        enemy.hand = [card]
        player = Character("Hero", 10, 5, 5)
        take_turn(enemy, player)
        # Enemy should discard hand and player HP unchanged
        self.assertEqual(player.hp, 10)
        self.assertEqual(len(enemy.hand), 4)  # refilled to full hand


if __name__ == "__main__":
    unittest.main()
