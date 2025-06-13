from battle.engine import create_basic_cards, run_battle
from battle.gui import BattleGUI
from battle.dungeon_gui import DungeonBattleGUI
from start_menu import run_start_menu
from characters import Character
from items import create_basic_items


def build_sample_character(name):
    deck = create_basic_cards() * 10  # 40 cards total
    items = create_basic_items()[:2]
    return Character(name=name, hp=30, mana=10, stamina=10, deck=deck, items=items)


def main():
    mode = run_start_menu()
    if not mode:
        print("Exited from start menu.")
        return

    player = build_sample_character("Player")
    enemy = build_sample_character("Enemy")

    if mode == "gui":
        BattleGUI(player, enemy).start()
    elif mode == "dungeon":
        DungeonBattleGUI(player, enemy).start()
    else:
        run_battle(player, enemy)


if __name__ == "__main__":
    main()
