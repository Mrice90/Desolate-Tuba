import argparse

from battle.engine import create_basic_cards, run_battle
from battle.gui import BattleGUI
from start_menu import run_start_menu
from characters import Character


def build_sample_character(name):
    deck = create_basic_cards() * 10  # 40 cards total
    return Character(name=name, hp=30, mana=10, stamina=10, deck=deck)


def main():
    parser = argparse.ArgumentParser(description="Medieval Duel demo")
    parser.add_argument("--gui", action="store_true", help="Run graphical interface")
    args = parser.parse_args()

    player = build_sample_character("Player")
    enemy = build_sample_character("Enemy")

    if args.gui:
        if run_start_menu():
            BattleGUI(player, enemy).start()
        else:
            print("Exited from start menu.")
    else:
        run_battle(player, enemy)


if __name__ == "__main__":
    main()
