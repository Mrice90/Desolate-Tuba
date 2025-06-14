import tkinter as tk
from bestiary import BESTIARY
from ui.fullscreen import create_fullscreen_root


def _show_monster(monster, parent):
    """Display a pop-up window with the monster's details."""
    win = tk.Toplevel(parent)
    win.title(monster["name"])

    text = tk.Text(win, wrap="word", width=60, height=15)
    text.pack(fill="both", expand=True)

    entry = (
        f"{monster['name']}\n{monster['description']}\n"
        f"HP:{monster['hp']} DMG:{monster['damage']}\n"
        f"STR:{monster['str']} THAU:{monster['thaumaturgy']} "
        f"AGI:{monster['agi']} RES:{monster['res']}\n"
        f"Tactics: {monster['tactics']}"
    )
    text.insert("end", entry)
    text.configure(state="disabled")

    tk.Button(win, text="Close", command=win.destroy).pack(pady=5)


def show_bestiary():
    """Display bestiary entries as a clickable list of names."""
    root = create_fullscreen_root("Bestiary")

    frame = tk.Frame(root)
    frame.pack(fill="both", expand=True)

    listbox = tk.Listbox(frame)
    scrollbar = tk.Scrollbar(frame, orient="vertical", command=listbox.yview)
    listbox.configure(yscrollcommand=scrollbar.set)
    listbox.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    for monster in BESTIARY:
        listbox.insert("end", monster["name"])

    def on_select(event):
        idxs = listbox.curselection()
        if idxs:
            _show_monster(BESTIARY[idxs[0]], root)

    listbox.bind("<Double-Button-1>", on_select)

    tk.Button(root, text="Close", command=root.destroy).pack(pady=5)
    root.mainloop()


if __name__ == "__main__":
    show_bestiary()
