import os
import tkinter as tk
from tkinter import messagebox
from ui.fullscreen import create_fullscreen_root
from save_system import save_game, load_game


def run_title_menu():
    """Display the initial title screen with new/load/continue options."""
    selection = {"mode": None, "player": None}

    def choose_new():
        selection["mode"] = "new"
        root.quit()

    def choose_load():
        selection["mode"] = "load"
        root.quit()

    def choose_continue():
        selection["mode"] = "continue"
        root.quit()

    root = create_fullscreen_root("Medieval Duel")
    tk.Label(root, text="Medieval Duel", font=("Arial", 48)).pack(pady=40)

    btn_opts = {"font": ("Arial", 24), "width": 15, "height": 2}

    if os.path.exists("savegame.json"):
        tk.Button(root, text="Continue", command=choose_continue, **btn_opts).pack(pady=10)

    tk.Button(root, text="New Game", command=choose_new, **btn_opts).pack(pady=10)
    tk.Button(root, text="Load Game", command=choose_load, **btn_opts).pack(pady=10)
    tk.Button(root, text="Quit", command=root.quit, **btn_opts).pack(pady=10)

    root.mainloop()
    root.destroy()
    return selection


def run_start_menu(player=None):
    """Display the in-game menu for selecting mode or saving/loading.

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

    def save():
        if player is None:
            messagebox.showinfo("Save", "No player data to save.")
            return
        save_game(player)
        messagebox.showinfo("Save", "Game saved.")

    def load():
        loaded = load_game()
        if loaded is None:
            messagebox.showerror("Load", "No save file found.")
            return
        selection["mode"] = "loadgame"
        selection["player"] = loaded
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
    tk.Button(root, text="Save Game", command=save, **btn_opts).pack(pady=10)
    tk.Button(root, text="Load Game", command=load, **btn_opts).pack(pady=10)
    tk.Button(root, text="Quit", command=exit_game, **btn_opts).pack(pady=10)

    root.mainloop()
    root.destroy()
    return selection["mode"]


if __name__ == "__main__":
    result = run_title_menu()
    print(result)
