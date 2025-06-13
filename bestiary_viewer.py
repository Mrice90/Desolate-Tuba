import tkinter as tk
from bestiary import BESTIARY


def show_bestiary():
    """Display all bestiary entries in a scrollable window."""
    root = tk.Tk()
    root.title("Bestiary")

    text = tk.Text(root, wrap="word", width=60)
    scrollbar = tk.Scrollbar(root, command=text.yview)
    text.configure(yscrollcommand=scrollbar.set)
    text.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    for monster in BESTIARY:
        entry = f"{monster['name']}\n{monster['description']}\n" \
                f"HP:{monster['hp']} DMG:{monster['damage']}\n" \
                f"STR:{monster['str']} THAU:{monster['thaumaturgy']} " \
                f"AGI:{monster['agi']} RES:{monster['res']}\n" \
                f"Tactics: {monster['tactics']}\n\n"
        text.insert("end", entry)

    tk.Button(root, text="Close", command=root.destroy).pack(pady=5)
    root.mainloop()


if __name__ == "__main__":
    show_bestiary()
