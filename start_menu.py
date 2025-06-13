import tkinter as tk


def run_start_menu():
    """Display a menu for choosing the game mode.

    Returns "console" for the text battle, "gui" for the graphical battle,
    or None if the user closes the menu without making a choice.
    """
    selection = {"mode": None}

    def choose_console():
        selection["mode"] = "console"
        root.quit()

    def choose_gui():
        selection["mode"] = "gui"
        root.quit()

    def exit_game():
        root.quit()

    root = tk.Tk()
    root.title("Medieval Duel - Main Menu")

    tk.Label(root, text="Medieval Duel", font=("Arial", 24)).pack(pady=10)

    tk.Button(root, text="Console Battle", width=20, command=choose_console).pack(pady=5)
    tk.Button(root, text="GUI Battle", width=20, command=choose_gui).pack(pady=5)
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
