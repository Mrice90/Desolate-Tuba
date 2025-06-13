import tkinter as tk
from battle.engine import simple_damage, simple_heal, gain_resource
from effects.status_effects import StatBuff, DodgeBuff, CounterSpell
from characters import Character
from items import create_basic_items
from cards import Card
from character_cards import CHARACTER_CARDS, UNIVERSAL_CARDS, CHARACTER_PROGRESSIONS


def _scale_amount(cost, resource):
    """Return a simple damage/heal amount based on card cost and resource."""
    if resource.lower() == "mana":
        return max(1, cost * 3)
    return max(1, cost * 2)


_EFFECT_MAP = {
    "strike": lambda: ("Damage", lambda u, t: simple_damage(u, t, 3)),
    "defend": lambda: ("Buff", lambda u, t: u.add_effect(StatBuff("Defend", 1, "armor", 4))),
    "meditate": lambda: ("Utility", lambda u, t: gain_resource(u, "mana", 3)),
    "dash": lambda: ("Utility", lambda u, t: u.add_effect(DodgeBuff("Dash", 2, 20))),
    "focus blast": lambda: ("Damage", lambda u, t: simple_damage(u, t, 5, "thaumaturgy")),
    "quick draw": lambda: ("Utility", lambda u, t: (u.draw_card(), u.draw_card())),
    "power surge": lambda: ("Buff", lambda u, t: u.add_effect(StatBuff("Power Surge", 2, "strength_mod", 2))),
    "second wind": lambda: ("Utility", lambda u, t: simple_heal(u, u, 5)),
    "counter spell": lambda: ("Utility", lambda u, t: u.add_effect(CounterSpell())),
    "inspire": lambda: ("Buff", lambda u, t: (u.add_effect(StatBuff("InspireSTR", 2, "strength_mod", 1)), u.add_effect(StatBuff("InspireAGI", 2, "agility_mod", 1)))),
}


def _make_card(info):
    """Convert a card info dictionary to a Card with scaled placeholder effects."""
    name_key = info["name"].lower()
    if name_key in _EFFECT_MAP:
        ctype, effect = _EFFECT_MAP[name_key]()
    else:
        ctype = info.get("type")
        if ctype is None:
            purpose = info.get("purpose", "").lower()
            if "offense" in purpose:
                ctype = "Damage"
            elif "recovery" in purpose or "buff" in purpose or "support" in purpose:
                ctype = "Buff"
            else:
                ctype = "Utility"
        amount = _scale_amount(info.get("cost", 1), info.get("resource", "stamina"))
        if ctype == "Damage":
            effect = lambda u, t, a=amount: simple_damage(u, t, a)
        elif ctype == "Buff":
            effect = lambda u, t, a=amount: simple_heal(u, u, a)
        else:
            effect = lambda u, t: None
    card = Card(info["name"], info.get("cost", 1), info.get("resource", "stamina").lower(), effect, info.get("effect", ""))
    card.type = ctype or ""
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
    # Two scrollable columns: available cards and current deck
    columns = tk.Frame(root)
    columns.pack(fill="both", expand=True, padx=10, pady=5)

    # Left column - available cards
    avail_container = tk.Frame(columns)
    avail_container.pack(side="left", fill="both", expand=True, padx=(0, 5))

    avail_canvas = tk.Canvas(avail_container)
    avail_scroll = tk.Scrollbar(avail_container, orient="vertical", command=avail_canvas.yview)
    avail_frame = tk.Frame(avail_canvas)

    avail_frame.bind(
        "<Configure>",
        lambda e: avail_canvas.configure(scrollregion=avail_canvas.bbox("all"))
    )

    avail_canvas.create_window((0, 0), window=avail_frame, anchor="nw")
    avail_canvas.configure(yscrollcommand=avail_scroll.set)

    avail_canvas.pack(side="left", fill="both", expand=True)
    avail_scroll.pack(side="right", fill="y")

    tk.Label(avail_frame, text="Available Cards (max 2 of each)").pack(anchor="w")

    # Right column - deck list
    deck_container = tk.Frame(columns)
    deck_container.pack(side="left", fill="both", expand=True, padx=(5, 0))

    deck_canvas = tk.Canvas(deck_container)
    deck_scroll = tk.Scrollbar(deck_container, orient="vertical", command=deck_canvas.yview)
    deck_frame = tk.Frame(deck_canvas)

    deck_frame.bind(
        "<Configure>",
        lambda e: deck_canvas.configure(scrollregion=deck_canvas.bbox("all"))
    )

    deck_canvas.create_window((0, 0), window=deck_frame, anchor="nw")
    deck_canvas.configure(yscrollcommand=deck_scroll.set)

    deck_canvas.pack(side="left", fill="both", expand=True)
    deck_scroll.pack(side="right", fill="y")

    tk.Label(deck_frame, text="Your Deck").pack(anchor="w")

    count_vars = {}

    def refresh_deck_display():
        for widget in deck_frame.winfo_children()[1:]:
            widget.destroy()
        for card in deck:
            row = tk.Frame(deck_frame, bd=1, relief="solid", padx=2, pady=2)
            row.pack(fill="x", pady=2)
            tk.Label(row, text=card.name, anchor="w").grid(row=0, column=0, sticky="w")
            tk.Button(row, text="-", width=2,
                      command=lambda c=card: remove_card(c)).grid(row=0, column=1, sticky="e")

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
        refresh_deck_display()

    def remove_card(card):
        if card_counts[card.name] <= 0:
            return
        card_counts[card.name] -= 1
        for i, c in enumerate(deck):
            if c.name == card.name:
                deck.pop(i)
                break
        update_labels()
        refresh_deck_display()

    for c in card_prototypes:
        row = tk.Frame(avail_frame, bd=1, relief="solid", padx=2, pady=2)
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

    refresh_deck_display()

    deck_label = tk.Label(root, text="Deck size: 0/20")
    deck_label.pack(pady=5)

    def finish():
        if len(deck) == 20:
            root.quit()

    start_btn = tk.Button(root, text="Start Game", state="disabled", command=finish)
    start_btn.pack(pady=5)

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
