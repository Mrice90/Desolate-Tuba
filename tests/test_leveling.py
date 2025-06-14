import unittest

from deck_builder import get_deck_size_for_level, get_unique_unlocks_for_level
from battle.engine import simple_damage
from characters import Character
from cards import Card

class TestLevelSystems(unittest.TestCase):
    def test_deck_size_progression(self):
        self.assertEqual(get_deck_size_for_level(1), 3)
        self.assertEqual(get_deck_size_for_level(5), 8)
        self.assertEqual(get_deck_size_for_level(10), 15)
        self.assertEqual(get_deck_size_for_level(20), 35)

    def test_unique_unlock_progression(self):
        self.assertEqual(get_unique_unlocks_for_level(1), 3)
        self.assertEqual(get_unique_unlocks_for_level(5), 8)
        self.assertEqual(get_unique_unlocks_for_level(10), 15)
        self.assertEqual(get_unique_unlocks_for_level(20), 35)

    def test_resilience_reduces_damage(self):
        attacker = Character("A", 10, 10, 10)
        defender = Character("B", 10, 10, 10, resilience_mod=2)
        card = Card("Hit", 1, "stamina", lambda u,t: None)
        simple_damage(attacker, defender, 5)
        # damage should be 5 - 2 resilience
        self.assertEqual(defender.hp, 7)

if __name__ == '__main__':
    unittest.main()
