from battle.engine import create_basic_cards, run_battle
from characters import Character


def build_sample_character(name):
    deck = create_basic_cards() * 10  # 40 cards total
    return Character(name=name, hp=30, mana=10, stamina=10, deck=deck)


def main():
    player = build_sample_character("Player")
    enemy = build_sample_character("Enemy")
    run_battle(player, enemy)


if __name__ == "__main__":
    main()
