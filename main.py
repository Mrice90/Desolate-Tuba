from battle.engine import create_basic_cards, run_battle
from battle.dungeon_gui import DungeonBattleGUI
from player_sheet import run_player_sheet
from start_menu import run_start_menu, run_title_menu
from save_system import load_game
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
    choice = run_title_menu()
    if not choice:
        return
    if choice["mode"] == "load" or choice["mode"] == "continue":
        player = load_game()
    if player is None:
        player = run_player_sheet()

    result = run_start_menu(player)
    mode = result.get("mode")
    if result.get("player") is not None:
        player = result.get("player")

    while mode:
        if mode == "playersheet":
            player = run_player_sheet(player)
        elif mode == "bestiary":
            show_bestiary()
        elif mode == "library":
            show_card_library()
        else:
            if player is None:
                player = run_player_sheet()
            if mode == "dungeon":
                player = run_player_sheet(player)
                continue_dungeon = True
                while continue_dungeon:
                    enemy = create_enemy_for_level(player.level)
                    gui = DungeonBattleGUI(player, enemy)
                    gui.start()
                    continue_dungeon = gui.continue_dungeon
            else:
                enemy = build_sample_character("Enemy")
                run_battle(player, enemy)

        result = run_start_menu(player)
        if result.get("player") is not None:
            player = result["player"]
        mode = result.get("mode")


if __name__ == "__main__":
    main()
