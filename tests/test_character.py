import random
import unittest

from characters import Character
from cards import Card
from effects.status_effects import DamageOverTime


class TestCharacterSystems(unittest.TestCase):
    def test_draw_and_shuffle(self):
        card_a = Card("A", 0, "mana", lambda u, t: None)
        card_b = Card("B", 0, "mana", lambda u, t: None)
        deck = [card_a, card_a, card_b, card_b]
        random.seed(0)
        char = Character("Test", 10, 10, 10, deck=deck[:])
        for _ in range(4):
            char.draw_card()
        self.assertEqual(len(char.deck), 0)
        self.assertEqual(len(char.hand), 4)
        char.discard_hand()
        self.assertEqual(len(char.hand), 4)  # hand refilled

    def test_gain_xp_and_level(self):
        prog = {"hp_per_level": 1, "mana_per_level": 1, "stamina_per_level": 1}
        char = Character("Hero", 10, 10, 10, progression=prog)
        char.gain_xp(150)
        self.assertEqual(char.level, 2)
        self.assertLess(char.xp, char.xp_to_next)

    def test_status_effect_damage(self):
        enemy = Character("Enemy", 10, 10, 10)
        effect = DamageOverTime("Burn", 2, 1)
        enemy.add_effect(effect)
        enemy.update_effects()
        self.assertEqual(enemy.hp, 9)
        enemy.update_effects()
        self.assertEqual(enemy.hp, 8)
        self.assertEqual(len(enemy.effects), 0)


if __name__ == "__main__":
    unittest.main()
