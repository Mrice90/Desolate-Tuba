import tkinter as tk
from battle.engine import simple_damage, simple_heal, gain_resource
from effects.status_effects import (
    StatBuff,
    DodgeBuff,
    CounterSpell,
    DamageOverTime,
    Stun,
    DamageNegate,
    PhoenixPact,
)
from characters import Character
from cards import Card
from character_cards import UNIVERSAL_CARDS


# Level based deck size --------------------------------------------------
_LEVEL_INCREMENTS = {1: 3, 2: 1, 3: 1, 4: 1, 5: 2, 6: 1, 7: 1, 8: 2, 9: 1, 10: 2}

def get_deck_size_for_level(level: int) -> int:
    """Return total number of cards allowed for a given level."""
    total = 0
    for lv in range(1, level + 1):
        if lv <= 10:
            total += _LEVEL_INCREMENTS.get(lv, 0)
        else:
            total += 2
    return min(35, total)


def _scale_amount(cost, resource):
    """Return a simple damage/heal amount based on card cost and resource."""
    if resource.lower() == "mana":
        return max(1, cost * 3)
    return max(1, cost * 2)


def _swap_top_two(deck):
    if len(deck) >= 2:
        deck[0], deck[1] = deck[1], deck[0]


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
    "spirit lash": lambda: ("Damage", lambda u, t: simple_damage(u, t, 2 + (1 if t.effects else 0), "thaumaturgy")),
    "cunning step": lambda: ("Utility", lambda u, t: (u.add_effect(StatBuff("Cunning Step", 2, "agility_mod", 2)), u.draw_card())),
    "magic sparkle": lambda: ("Utility", lambda u, t: (print(f"Top enemy card: {t.deck[0].name}" if t.deck else "Enemy deck empty"))),
    "guarded heart": lambda: ("Buff", lambda u, t: u.add_effect(StatBuff("Guarded Heart", 2, "resilience_mod", 1))),
    "force push": lambda: ("Damage", lambda u, t: (simple_damage(u, t, 2, "strength"), print(f"{t.name} is pushed back!"))),
    "fade step": lambda: ("Utility", lambda u, t: u.add_effect(DodgeBuff("Fade Step", 1, 20))),
    "stone grip": lambda: ("Debuff", lambda u, t: t.add_effect(StatBuff("Stone Grip", 1, "agility_mod", -1))),
    "ember shot": lambda: ("Damage", lambda u, t: (simple_damage(u, t, 2, "thaumaturgy"), __import__('random').randint(1,100) <= 15 and t.add_effect(DamageOverTime("Burn", 2, 1)))),
    "tactic study": lambda: ("Utility", lambda u, t: _swap_top_two(u.deck)),
    "intimidate": lambda: ("Debuff", lambda u, t: t.add_effect(StatBuff("Intimidated", 2, "strength_mod", -1))),

    "whirlwind slash": lambda: ("Damage", lambda u, t: (simple_damage(u, t, 3, "strength"), (__import__('random').randint(1,100) <= 25 and t.add_effect(StatBuff("Weaken", 2, "strength_mod", -1))))),
    "ice shards": lambda: ("Damage", lambda u, t: (simple_damage(u, t, 2, "thaumaturgy"), t.add_effect(Stun("Freeze", 2)))),
    "barrier bubble": lambda: ("Buff", lambda u, t: u.add_effect(DamageNegate("Barrier Bubble", 99))),
    "acrobat's spin": lambda: ("Utility", lambda u, t: ([u.remove_effect(e.name) for e in list(u.effects) if isinstance(e, StatBuff) and e.attr=="agility_mod" and e.amount<0], u.add_effect(StatBuff("Acrobat's Spin", 2, "agility_mod", 2)))),
    "shock infusion": lambda: ("Damage", lambda u, t: simple_damage(u, t, (6 if getattr(t, 'stunned', False) else 3), "thaumaturgy")),

    "cataclysm blast": lambda: ("Damage", lambda u, t: simple_damage(u, t, 9 if len(t.effects)>=2 else 7, "thaumaturgy")),
    "phoenix pact": lambda: ("Utility", lambda u, t: u.add_effect(PhoenixPact("Phoenix Pact", 2))),
    "storm avatar": lambda: ("Buff", lambda u, t: (u.add_effect(StatBuff("StormAvatarSTR", 2, "strength_mod", 2)), u.add_effect(StatBuff("StormAvatarAGI", 2, "agility_mod", 2)), u.add_effect(StatBuff("StormAvatarRES", 2, "resilience_mod", 2)))),
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
            effect = lambda u, t, a=amount: u.add_effect(StatBuff(info.get("name", "Buff"), 2, "strength_mod", a))
        elif ctype == "Debuff":
            effect = lambda u, t, a=amount: t.add_effect(StatBuff(info.get("name", "Debuff"), 2, "strength_mod", -a))
        elif ctype == "Summon":
            from summons import Summon
            effect = lambda u, t, a=amount: u.add_summon(Summon(info.get("name", "Summon"), a, info.get("duration", 3)))
        else:
            effect = lambda u, t: None
    card = Card(
        info["name"],
        info.get("cost", 1),
        info.get("resource", "stamina").lower(),
        effect,
        info.get("effect", ""),
        card_type=ctype or "",
        rarity=info.get("rarity", "common"),
        level_requirement=info.get("level", 1),
        stat_requirements=info.get("stats", {}),
        category=info.get("category", "universal"),
    )
    return card


def run_deck_builder_menu(player: Character):
    """Edit ``player`` deck using the universal card pool."""
    level = player.level
    deck_limit = get_deck_size_for_level(level)
    card_infos = UNIVERSAL_CARDS
    card_prototypes = [_make_card(c) for c in card_infos]

    root = tk.Tk()
    root.title(f"Deck Builder - {player.name}")

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
        deck_label.config(text=f"Deck size: {len(deck)}/{deck_limit}")
        for name, var in count_vars.items():
            var.set(str(card_counts[name]))
        if len(deck) == deck_limit:
            start_btn.config(state="normal")
        else:
            start_btn.config(state="disabled")

    def add_card(card):
        if card_counts[card.name] >= 2 or len(deck) >= deck_limit:
            return
        if level < card.level_requirement:
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

        info = f"{c.name} ({c.card_type}) - Cost: {c.cost} {c.resource_type}\n{c.description}"
        tk.Label(row, text=info, justify="left", anchor="w", wraplength=400).grid(row=0, column=0, sticky="w")

        ctrl = tk.Frame(row)
        ctrl.grid(row=0, column=1, sticky="e", padx=5)

        count_var = tk.StringVar(value="0")
        count_vars[c.name] = count_var
        tk.Label(ctrl, textvariable=count_var, width=2).pack(side="left")
        tk.Button(ctrl, text="+", command=lambda card=c: add_card(card), width=2).pack(side="left")
        tk.Button(ctrl, text="-", command=lambda card=c: remove_card(card), width=2).pack(side="left")

    refresh_deck_display()

    deck_label = tk.Label(root, text=f"Deck size: 0/{deck_limit}")
    deck_label.pack(pady=5)

    def finish():
        if len(deck) == deck_limit:
            root.quit()

    start_btn = tk.Button(root, text="Save", state="disabled", command=finish)
    start_btn.pack(pady=5)

    update_labels()
    root.mainloop()
    root.destroy()

    player.deck = deck
    player.discard_pile = []
    player.hand = []
    return player
