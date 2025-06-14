import tkinter as tk
from character_cards import CHARACTER_CARDS, UNIVERSAL_CARDS
from ui.fullscreen import create_fullscreen_root


def _show_card(card, parent):
    """Display details for a single card."""
    win = tk.Toplevel(parent)
    win.title(card["name"])

    text = tk.Text(win, wrap="word", width=60, height=10)
    text.pack(fill="both", expand=True)

    entry = (
        f"{card['name']} ({card['type']}, cost {card['cost']} {card['resource']})\n"
        f"{card['effect']}"
    )
    text.insert("end", entry)
    text.configure(state="disabled")

    tk.Button(win, text="Close", command=win.destroy).pack(pady=5)


def show_card_library():
    """Display all card names in a scrollable list."""
    root = create_fullscreen_root("Card Library")

    frame = tk.Frame(root)
    frame.pack(fill="both", expand=True)

    listbox = tk.Listbox(frame)
    scrollbar = tk.Scrollbar(frame, orient="vertical", command=listbox.yview)
    listbox.configure(yscrollcommand=scrollbar.set)
    listbox.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    all_cards = []
    for card in UNIVERSAL_CARDS:
        all_cards.append(card)
        listbox.insert("end", card["name"])

    for name, data in CHARACTER_CARDS.items():
        for card in data["cards"]:
            label = f"{card['name']} ({name})"
            all_cards.append(card)
            listbox.insert("end", label)

    def on_select(event):
        idxs = listbox.curselection()
        if idxs:
            _show_card(all_cards[idxs[0]], root)

    listbox.bind("<Double-Button-1>", on_select)

    tk.Button(root, text="Close", command=root.destroy).pack(pady=5)
    root.mainloop()


if __name__ == "__main__":
    show_card_library()
