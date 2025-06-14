import tkinter as tk
from characters import Character
from deck_builder import run_deck_builder_menu
from items import create_basic_items

_ICON_OPTIONS = ["@", "#", "&", "%"]

_DEFAULT_PROGRESSION = {
    "base_hp": 20, "hp_per_level": 3,
    "base_mana": 10, "mana_per_level": 2,
    "base_stamina": 10, "stamina_per_level": 2,
    "hp_regen": 1.0, "hp_regen_per_level": 0.1,
    "mana_regen": 1.0, "mana_regen_per_level": 0.1,
    "stamina_regen": 1.0, "stamina_regen_per_level": 0.1,
}


def _new_character():
    prog = _DEFAULT_PROGRESSION
    return Character(
        name="Adventurer",
        hp=prog["base_hp"],
        mana=prog["base_mana"],
        stamina=prog["base_stamina"],
        items=create_basic_items()[:2],
        progression=prog,
        icon=_ICON_OPTIONS[0],
    )


def run_player_sheet(player: Character | None = None) -> Character:
    """Display a player sheet for viewing and editing ``player``."""
    if player is None:
        player = _new_character()

    root = tk.Tk()
    root.title("Player Sheet")

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

    tk.Button(root, text="Edit Deck", command=edit_deck).pack(pady=5)

    def _confirm():
        player.name = name_var.get()
        player.icon = icon_var.get()
        root.quit()

    tk.Button(root, text="Confirm", command=_confirm).pack(pady=5)
    root.mainloop()
    root.destroy()
    return player

if __name__ == "__main__":
    run_player_sheet()
