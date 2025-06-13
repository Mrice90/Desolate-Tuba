import tkinter as tk
from battle.engine import create_basic_cards
from characters import Character
from items import create_basic_items
from cards import Card


def run_deck_builder_menu():
    """Interactive menu for selecting a character and building a 20 card deck."""
    characters = {
        "Wizard": dict(hp=25, mana=15, stamina=8),
        "Warrior": dict(hp=35, mana=5, stamina=15),
        "Rogue": dict(hp=28, mana=10, stamina=12),
    }
    card_prototypes = create_basic_cards()

    root = tk.Tk()
    root.title("Deck Builder")

    selected_character = tk.StringVar(value=list(characters.keys())[0])
    deck = []
    card_counts = {c.name: 0 for c in card_prototypes}

    # --- Character selection --------------------------------------------
    char_frame = tk.Frame(root)
    char_frame.pack(padx=10, pady=5, fill="x")
    tk.Label(char_frame, text="Choose Character:").pack(anchor="w")
    for name in characters:
        tk.Radiobutton(char_frame, text=name, variable=selected_character,
                       value=name).pack(anchor="w")

    # --- Card selection --------------------------------------------------
    card_frame = tk.Frame(root)
    card_frame.pack(padx=10, pady=5)
    tk.Label(card_frame, text="Build Deck (max 2 of each card)").pack()

    count_vars = {}

    def update_labels():
        deck_label.config(text=f"Deck size: {len(deck)}/20")
        for name, var in count_vars.items():
            var.set(str(card_counts[name]))

    def add_card(card):
        if card_counts[card.name] >= 2 or len(deck) >= 20:
            return
        card_counts[card.name] += 1
        deck.append(Card(card.name, card.cost, card.resource_type,
                         card.effect_function, card.description))
        update_labels()

    def remove_card(card):
        if card_counts[card.name] <= 0:
            return
        card_counts[card.name] -= 1
        for i, c in enumerate(deck):
            if c.name == card.name:
                deck.pop(i)
                break
        update_labels()

    for c in card_prototypes:
        row = tk.Frame(card_frame)
        row.pack(fill="x", pady=2)
        tk.Label(row, text=f"{c.name} ({c.cost} {c.resource_type})").pack(side="left")
        count_var = tk.StringVar(value="0")
        count_vars[c.name] = count_var
        tk.Label(row, textvariable=count_var, width=3).pack(side="left")
        tk.Button(row, text="+", command=lambda card=c: add_card(card)).pack(side="left")
        tk.Button(row, text="-", command=lambda card=c: remove_card(card)).pack(side="left")

    deck_label = tk.Label(root, text="Deck size: 0/20")
    deck_label.pack(pady=5)

    def finish():
        if len(deck) == 20:
            root.quit()

    tk.Button(root, text="Start Game", command=finish).pack(pady=5)
    root.mainloop()
    root.destroy()

    stats = characters[selected_character.get()]
    return Character(name=selected_character.get(),
                     hp=stats["hp"], mana=stats["mana"], stamina=stats["stamina"],
                     deck=deck, items=create_basic_items()[:2])
