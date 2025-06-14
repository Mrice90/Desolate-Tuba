import tkinter as tk
from ui.fullscreen import create_fullscreen_root


def run_start_menu():
    """Display a menu for choosing the game mode.

    Returns one of:
    ``"dungeon"`` for the dungeon crawler,
    ``"playersheet"`` to open the player sheet,
    ``"bestiary"`` to view the bestiary,
    ``"library"`` to view the card library,
    or ``None`` if the user closes the menu without making a choice.
    """
    selection = {"mode": None}

    def choose_dungeon():
        selection["mode"] = "dungeon"
        root.quit()

    def choose_deckbuilder():
        selection["mode"] = "playersheet"
        root.quit()

    def choose_bestiary():
        selection["mode"] = "bestiary"
        root.quit()

    def choose_library():
        selection["mode"] = "library"
        root.quit()

    def exit_game():
        root.quit()

    root = create_fullscreen_root("Medieval Duel - Main Menu")

    tk.Label(root, text="Medieval Duel", font=("Arial", 36)).pack(pady=20)

    btn_opts = {"font": ("Arial", 20), "width": 20, "height": 2}
    tk.Button(root, text="Dungeon Battle", command=choose_dungeon, **btn_opts).pack(pady=10)
    tk.Button(root, text="Player Sheet", command=choose_deckbuilder, **btn_opts).pack(pady=10)
    tk.Button(root, text="Bestiary", command=choose_bestiary, **btn_opts).pack(pady=10)
    tk.Button(root, text="Card Library", command=choose_library, **btn_opts).pack(pady=10)
    tk.Button(root, text="Quit", command=exit_game, **btn_opts).pack(pady=10)

    root.mainloop()
    root.destroy()
    return selection["mode"]


if __name__ == "__main__":
    mode = run_start_menu()
    if mode:
        print(f"Selected mode: {mode}")
    else:
        print("Quit from start menu")
