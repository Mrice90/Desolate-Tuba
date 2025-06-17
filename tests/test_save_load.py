import os
import tempfile
import unittest
from characters import Character
from character_cards import CHARACTER_CARDS
from deck_builder import _make_card
from save_system import save_game, load_game

class TestSaveLoad(unittest.TestCase):
    def test_unique_library_persisted(self):
        infos = CHARACTER_CARDS['Aurelia Flameheart']['cards']
        library = [_make_card(i) for i in infos]
        player = Character('Aurelia Flameheart', 10, 10, 10,
                           unique_library=library)
        player.unlocked_unique_cards = library[:2]
        player.deck = library[:20]
        with tempfile.TemporaryDirectory() as d:
            path = os.path.join(d, 'save.json')
            save_game(player, path)
            loaded = load_game(path)
            self.assertEqual(len(loaded.unique_library), len(library))
            self.assertEqual(
                {c.name for c in loaded.unlocked_unique_cards},
                {c.name for c in player.unlocked_unique_cards}
            )

if __name__ == '__main__':
    unittest.main()
