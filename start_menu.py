import tkinter as tk


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

    root = tk.Tk()
    root.title("Medieval Duel - Main Menu")

    tk.Label(root, text="Medieval Duel", font=("Arial", 24)).pack(pady=10)

    tk.Button(root, text="Dungeon Battle", width=20, command=choose_dungeon).pack(pady=5)
    tk.Button(root, text="Player Sheet", width=20, command=choose_deckbuilder).pack(pady=5)
    tk.Button(root, text="Bestiary", width=20, command=choose_bestiary).pack(pady=5)
    tk.Button(root, text="Card Library", width=20, command=choose_library).pack(pady=5)
    tk.Button(root, text="Quit", width=20, command=exit_game).pack(pady=5)

    root.mainloop()
    root.destroy()
    return selection["mode"]


if __name__ == "__main__":
    mode = run_start_menu()
    if mode:
        print(f"Selected mode: {mode}")
    else:
        print("Quit from start menu")
