from battle.engine import create_basic_cards, run_battle
from battle.dungeon_gui import DungeonBattleGUI
from deck_builder import run_deck_builder_menu
from start_menu import run_start_menu
from characters import Character
from items import create_basic_items
from bestiary import create_enemy_for_level
from bestiary_viewer import show_bestiary
from card_library_viewer import show_card_library


def build_sample_character(name):
    deck = create_basic_cards() * 10  # 40 cards total
    items = create_basic_items()[:2]
    return Character(name=name, hp=30, mana=10, stamina=10, deck=deck, items=items)


def main():
    player = None
    mode = run_start_menu()
    while mode:
        if mode == "deckbuilder":
            player = run_deck_builder_menu()
        elif mode == "bestiary":
            show_bestiary()
        elif mode == "library":
            show_card_library()
        else:
            if player is None:
                player = run_deck_builder_menu()
            if mode == "dungeon":
                continue_dungeon = True
                while continue_dungeon:
                    enemy = create_enemy_for_level(player.level)
                    gui = DungeonBattleGUI(player, enemy)
                    gui.start()
                    continue_dungeon = gui.continue_dungeon
            else:
                enemy = build_sample_character("Enemy")
                run_battle(player, enemy)

        mode = run_start_menu()


if __name__ == "__main__":
    main()
