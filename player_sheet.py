import tkinter as tk
from characters import Character
from deck_builder import run_deck_builder_menu, _make_card, get_unique_unlocks_for_level
from character_cards import CHARACTER_CARDS, CHARACTER_PROGRESSIONS
from items import create_basic_items
from ui.fullscreen import create_fullscreen_root

_ICON_OPTIONS = ["@", "#", "&", "%"]


_DEFAULT_CHARACTER = "Aurelia Flameheart"
_DEFAULT_PROGRESSION = CHARACTER_PROGRESSIONS[_DEFAULT_CHARACTER]


def _new_character():
    prog = _DEFAULT_PROGRESSION
    unique_pool = [
        _make_card(c) for c in CHARACTER_CARDS[_DEFAULT_CHARACTER]["cards"]
    ]
    return Character(
        name=_DEFAULT_CHARACTER,
        hp=prog["base_hp"],
        mana=prog["base_mana"],
        stamina=prog["base_stamina"],
        items=create_basic_items()[:2],
        progression=prog,
        icon=_ICON_OPTIONS[0],
        unique_library=unique_pool,
    )


def run_player_sheet(player: Character | None = None) -> Character:
    """Display a player sheet for viewing and editing ``player``."""
    if player is None:
        player = _new_character()

    root = create_fullscreen_root("Player Sheet")

    name_var = tk.StringVar(value=player.name)
    tk.Label(root, text="Name:").pack()
    tk.Entry(root, textvariable=name_var).pack()

    icon_var = tk.StringVar(value=player.icon)
    icon_frame = tk.Frame(root)
    icon_frame.pack(pady=5)
    icon_label = tk.Label(icon_frame, textvariable=icon_var, font=("Arial", 32))
    icon_label.pack(side="left")

    def _next_icon(step: int):
        idx = (_ICON_OPTIONS.index(icon_var.get()) + step) % len(_ICON_OPTIONS)
        icon_var.set(_ICON_OPTIONS[idx])

    tk.Button(icon_frame, text="<", command=lambda: _next_icon(-1)).pack(side="left")
    tk.Button(icon_frame, text=">", command=lambda: _next_icon(1)).pack(side="left")

    stats_frame = tk.Frame(root)
    stats_frame.pack(pady=5)

    def _refresh_stats():
        for w in stats_frame.winfo_children():
            w.destroy()
        tk.Label(stats_frame, text=f"Level: {player.level}  XP: {player.xp}/{player.xp_to_next}").pack()
        tk.Label(stats_frame, text=f"HP: {player.hp}/{player.max_hp}").pack()
        tk.Label(stats_frame, text=f"Mana: {player.mana}/{player.max_mana}").pack()
        tk.Label(stats_frame, text=f"Stamina: {player.stamina}/{player.max_stamina}").pack()
        tk.Label(stats_frame, text=(
            f"STR:{player.strength_mod} THA:{player.thaumaturgy_mod} "
            f"AGI:{player.agility_mod} RES:{player.resilience_mod}"
        )).pack()
        tk.Label(
            stats_frame,
            text=(
                f"Unique Cards: {len(player.unlocked_unique_cards)}/"
                f"{get_unique_unlocks_for_level(player.level)}"
            ),
        ).pack()
        if player.unspent_points:
            tk.Label(stats_frame, text=f"Unspent points: {player.unspent_points}").pack()

    _refresh_stats()

    def allocate(stat: str):
        if player.allocate_point(stat):
            _refresh_stats()

    alloc_frame = tk.Frame(root)
    alloc_frame.pack(pady=2)
    for stat in ["Strength", "Thaumaturgy", "Agility", "Resilience"]:
        tk.Button(alloc_frame, text=stat, command=lambda s=stat: allocate(s)).pack(side="left")

    def edit_deck():
        run_deck_builder_menu(player)
        _refresh_stats()

    def unlock_cards():
        allowed = get_unique_unlocks_for_level(player.level)
        remaining = allowed - len(player.unlocked_unique_cards)
        if remaining <= 0:
            tk.messagebox.showinfo("Unlocks", "No unique card unlocks available.")
            return
        root2 = create_fullscreen_root("Select Unique Cards")
        tk.Label(root2, text=f"Choose up to {remaining} cards").pack()
        frame = tk.Frame(root2)
        frame.pack(fill="both", expand=True)
        vars = []
        cards = [c for c in player.unique_library if c not in player.unlocked_unique_cards]
        if player.level == 1:
            cards = [c for c in cards if c.cost <= 1 and c.resource_type in ("mana", "stamina")]
        for card in cards:
            var = tk.IntVar()
            chk = tk.Checkbutton(frame, text=f"{card.name} (cost {card.cost} {card.resource_type})", variable=var, anchor="w", justify="left")
            chk.pack(fill="x", padx=5, pady=2)
            vars.append((var, card))
        def confirm():
            selected = [c for v, c in vars if v.get()]
            if len(selected) > remaining:
                tk.messagebox.showerror("Too many", "Select fewer cards.")
                return
            player.unlocked_unique_cards.extend(selected)
            root2.destroy()
            _refresh_stats()
        tk.Button(root2, text="Confirm", command=confirm).pack(pady=5)
        root2.mainloop()

    tk.Button(root, text="Edit Deck", command=edit_deck, font=("Arial", 16), height=2).pack(pady=10)
    tk.Button(root, text="Unlock Cards", command=unlock_cards, font=("Arial", 16), height=2).pack(pady=10)

    def _confirm():
        player.name = name_var.get()
        player.icon = icon_var.get()
        root.quit()

    tk.Button(root, text="Confirm", command=_confirm, font=("Arial", 16), height=2).pack(pady=10)
    root.mainloop()
    root.destroy()
    return player

if __name__ == "__main__":
    run_player_sheet()
