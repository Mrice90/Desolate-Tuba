import tkinter as tk
from character_cards import CHARACTER_CARDS, UNIVERSAL_CARDS


def show_card_library():
    """Display all cards in the library in a scrollable window."""
    root = tk.Tk()
    root.title("Card Library")

    text = tk.Text(root, wrap="word", width=60)
    scrollbar = tk.Scrollbar(root, command=text.yview)
    text.configure(yscrollcommand=scrollbar.set)
    text.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    text.insert("end", "Universal Cards\n")
    for card in UNIVERSAL_CARDS:
        entry = f"- {card['name']} ({card['type']}, cost {card['cost']} {card['resource']})\n  {card['effect']}\n"
        text.insert("end", entry)
    text.insert("end", "\n")

    for name, data in CHARACTER_CARDS.items():
        text.insert("end", f"{name} - {data['class']}\n")
        for card in data['cards']:
            entry = f"- {card['name']} ({card['type']}, cost {card['cost']} {card['resource']})\n  {card['effect']}\n"
            text.insert("end", entry)
        text.insert("end", "\n")

    tk.Button(root, text="Close", command=root.destroy).pack(pady=5)
    root.mainloop()


if __name__ == "__main__":
    show_card_library()
