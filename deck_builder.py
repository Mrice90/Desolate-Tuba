import tkinter as tk
from battle.engine import simple_damage, simple_heal
from characters import Character
from items import create_basic_items
from cards import Card
from character_cards import CHARACTER_CARDS, UNIVERSAL_CARDS, CHARACTER_PROGRESSIONS


def _make_card(info):
    """Convert a card info dictionary to a Card with simple placeholder effects."""
    if info.get("type") == "Damage":
        effect = lambda u, t: simple_damage(u, t, 5)
    elif info.get("type") == "Buff":
        effect = lambda u, t: simple_heal(u, u, 3)
    else:
        effect = lambda u, t: None
    card = Card(info["name"], info["cost"], info["resource"].lower(), effect, info.get("effect", ""))
    card.type = info.get("type", "")
    return card


def _choose_character():
    chars = list(CHARACTER_CARDS.keys())
    root = tk.Tk()
    root.title("Choose Character")
    selected = tk.StringVar(value=chars[0])
    tk.Label(root, text="Choose Character:").pack(anchor="w")
    for name in chars:
        cls = CHARACTER_CARDS[name]["class"]
        tk.Radiobutton(root, text=f"{name} ({cls})", variable=selected, value=name).pack(anchor="w")
    tk.Button(root, text="Next", command=root.quit).pack(pady=5)
    root.mainloop()
    root.destroy()
    return selected.get()


def run_deck_builder_menu():
    """Interactive menu for selecting a character and building a 20 card deck."""
    char_name = _choose_character()
    card_infos = UNIVERSAL_CARDS + CHARACTER_CARDS[char_name]["cards"]
    card_prototypes = [_make_card(c) for c in card_infos]

    progression = CHARACTER_PROGRESSIONS[char_name]

    root = tk.Tk()
    root.title(f"Deck Builder - {char_name}")

    deck = []
    card_counts = {c.name: 0 for c in card_prototypes}

    start_btn = None

    # --- Card selection --------------------------------------------------
    # Card list inside a scrollable canvas so long lists remain accessible
    list_container = tk.Frame(root)
    list_container.pack(fill="both", expand=True, padx=10, pady=5)

    canvas = tk.Canvas(list_container)
    scrollbar = tk.Scrollbar(list_container, orient="vertical", command=canvas.yview)
    scroll_frame = tk.Frame(canvas)

    scroll_frame.bind(
        "<Configure>",
        lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
    )

    canvas.create_window((0, 0), window=scroll_frame, anchor="nw")
    canvas.configure(yscrollcommand=scrollbar.set)

    canvas.pack(side="left", fill="both", expand=True)
    scrollbar.pack(side="right", fill="y")

    tk.Label(scroll_frame, text="Build Deck (max 2 of each card)").pack(anchor="w")

    count_vars = {}

    def update_labels():
        deck_label.config(text=f"Deck size: {len(deck)}/20")
        for name, var in count_vars.items():
            var.set(str(card_counts[name]))
        if len(deck) == 20:
            start_btn.config(state="normal")
        else:
            start_btn.config(state="disabled")

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
        row = tk.Frame(scroll_frame, bd=1, relief="solid", padx=2, pady=2)
        row.pack(fill="x", pady=2)

        info = f"{c.name} ({c.type}) - Cost: {c.cost} {c.resource_type}\n{c.description}"
        tk.Label(row, text=info, justify="left", anchor="w", wraplength=400).grid(row=0, column=0, sticky="w")

        ctrl = tk.Frame(row)
        ctrl.grid(row=0, column=1, sticky="e", padx=5)

        count_var = tk.StringVar(value="0")
        count_vars[c.name] = count_var
        tk.Label(ctrl, textvariable=count_var, width=2).pack(side="left")
        tk.Button(ctrl, text="+", command=lambda card=c: add_card(card), width=2).pack(side="left")
        tk.Button(ctrl, text="-", command=lambda card=c: remove_card(card), width=2).pack(side="left")

    deck_label = tk.Label(root, text="Deck size: 0/20")
    deck_label.pack(pady=5)

    start_btn = tk.Button(root, text="Start Game", state="disabled", command=finish)
    start_btn.pack(pady=5)

    def finish():
        if len(deck) == 20:
            root.quit()

    update_labels()
    root.mainloop()
    root.destroy()

    return Character(
        name=char_name,
        hp=progression["base_hp"],
        mana=progression["base_mana"],
        stamina=progression["base_stamina"],
        deck=deck,
        items=create_basic_items()[:2],
        hp_regen=progression["hp_regen"],
        mana_regen=progression["mana_regen"],
        stamina_regen=progression["stamina_regen"],
        progression=progression,
    )
