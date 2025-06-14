import tkinter as tk
from tkinter import messagebox
from enemy_ai import choose_card_index
from ui.fullscreen import create_fullscreen_root


class BattleGUI:
    """Graphical battle interface with quadrant command layout."""

    def __init__(self, player, enemy):
        self.player = player
        self.enemy = enemy
        self.root = create_fullscreen_root("Medieval Duel")

        # Log output
        self.log_text = tk.Text(self.root, height=10, state="disabled")
        self.log_text.pack(fill="both", expand=True)

        # Status labels
        self.enemy_label = tk.Label(self.root, text="")
        self.enemy_label.pack()
        self.player_label = tk.Label(self.root, text="")
        self.player_label.pack()
        self.deck_label = tk.Label(self.root, text="")
        self.deck_label.pack()

        # Frame for dynamic action buttons (cards/items)
        self.action_frame = tk.Frame(self.root)
        self.action_frame.pack(fill="x")

        # Option buttons in 2x2 grid
        self.option_frame = tk.Frame(self.root)
        self.option_frame.pack(fill="both", expand=True)

        btn_opts = {"font": ("Arial", 16), "height": 2}
        self.battle_btn = tk.Button(self.option_frame, text="Battle",
                                    command=self.show_hand, **btn_opts)
        self.items_btn = tk.Button(self.option_frame, text="Items",
                                   command=self.show_items, **btn_opts)
        self.fold_btn = tk.Button(self.option_frame, text="Fold",
                                  command=self.fold, **btn_opts)
        self.flee_btn = tk.Button(self.option_frame, text="Flee",
                                  command=self.flee, **btn_opts)

        self.battle_btn.grid(row=0, column=0, sticky="nsew", padx=5, pady=5)
        self.fold_btn.grid(row=0, column=1, sticky="nsew", padx=5, pady=5)
        self.items_btn.grid(row=1, column=0, sticky="nsew", padx=5, pady=5)
        self.flee_btn.grid(row=1, column=1, sticky="nsew", padx=5, pady=5)

        for i in range(2):
            self.option_frame.rowconfigure(i, weight=1)
            self.option_frame.columnconfigure(i, weight=1)

        self.card_buttons = []
        self.item_buttons = []
        self.victory = None

    def end_battle(self, won: bool):
        """Set battle result and close the window."""
        self.victory = won
        self.root.quit()

    # Utility methods -----------------------------------------------------
    def _stats(self, c):
        return f"{c.name} HP:{c.hp}/{c.max_hp} Mana:{c.mana}/{c.max_mana} " \
               f"Stamina:{c.stamina}/{c.max_stamina}"

    def log(self, msg):
        self.log_text.configure(state="normal")
        self.log_text.insert("end", msg + "\n")
        self.log_text.configure(state="disabled")
        self.log_text.see("end")

    def update_labels(self):
        self.enemy_label.configure(text=self._stats(self.enemy))
        self.player_label.configure(text=self._stats(self.player))
        self.deck_label.configure(
            text=f"Deck:{len(self.player.deck)} Discard:{len(self.player.discard_pile)}"
        )

    def clear_action_frame(self):
        for w in self.action_frame.winfo_children():
            w.destroy()
        self.card_buttons = []
        self.item_buttons = []

    # Player actions ------------------------------------------------------
    def show_hand(self):
        self.clear_action_frame()
        for idx, card in enumerate(self.player.hand):
            row, col = divmod(idx, 2)
            est = ""
            if getattr(card, "card_type", "").lower() == "damage":
                dmg = card.cost * (3 if card.resource_type == "mana" else 2)
                est = f"\nEst dmg: {dmg}"
            text = (
                f"{card.name}\nCost: {card.cost} {card.resource_type}\n"
                f"{card.description}{est}"
            )
            btn = tk.Button(
                self.action_frame,
                text=text,
                wraplength=200,
                justify="left",
                font=("Arial", 14),
                command=lambda i=idx: self.play_card(i),
            )
            btn.grid(row=row, column=col, sticky="nsew", padx=5, pady=5)
            self.card_buttons.append(btn)
        for i in range(2):
            self.action_frame.rowconfigure(i, weight=1)
            self.action_frame.columnconfigure(i, weight=1)
        tk.Button(
            self.action_frame,
            text="Back",
            command=self.clear_action_frame,
            font=("Arial", 14),
            height=2,
        ).grid(row=2, column=0, columnspan=2, pady=5)

    def show_items(self):
        self.clear_action_frame()
        if not self.player.items:
            tk.Label(self.action_frame, text="No items available").pack()
            return
        for idx, item in enumerate(self.player.items):
            text = item.name
            btn = tk.Button(
                self.action_frame,
                text=text,
                font=("Arial", 14),
                height=2,
                command=lambda i=idx: self.use_item(i),
            )
            btn.pack(fill="x", pady=5)
            self.item_buttons.append(btn)
        tk.Button(
            self.action_frame,
            text="Back",
            command=self.clear_action_frame,
            font=("Arial", 14),
            height=2,
        ).pack(fill="x", pady=5)

    def flee(self):
        import random
        if random.randint(1, 100) <= 50:
            self.log("You fled the battle!")
            messagebox.showinfo("Flee", "You fled the battle!")
            self.end_battle(False)
        else:
            self.log("Flee attempt failed!")
            self.end_player_turn()

    def fold(self):
        self.player.discard_hand()
        self.log("You fold and redraw your hand.")
        self.end_player_turn()

    def play_card(self, index):
        card = self.player.hand[index]
        enemy_hp_before = self.enemy.hp
        player_hp_before = self.player.hp
        if not self.player.play_card(index, self.enemy):
            self.log("Failed to play card!")
            return
        damage = max(0, enemy_hp_before - self.enemy.hp)
        heal = max(0, self.player.hp - player_hp_before)
        msg = f"You play {card.name}"
        if damage:
            msg += f" dealing {damage} damage"
        if heal:
            msg += f" and heal {heal} HP"
        self.log(msg)
        self.update_labels()
        if self.enemy.is_defeated():
            self.end_battle(True)
            return
        self.end_player_turn()

    def use_item(self, index):
        hp_before = self.player.hp
        mana_before = self.player.mana
        stamina_before = self.player.stamina
        if not self.player.use_item(index):
            return
        delta_hp = self.player.hp - hp_before
        delta_mana = self.player.mana - mana_before
        delta_stamina = self.player.stamina - stamina_before
        msg = f"You use {self.player.name}'s item"
        if delta_hp > 0:
            msg += f" and recover {delta_hp} HP"
        if delta_mana > 0:
            msg += f" and restore {delta_mana} mana"
        if delta_stamina > 0:
            msg += f" and restore {delta_stamina} stamina"
        self.log(msg)
        self.update_labels()
        self.end_player_turn()

    # Turn management -----------------------------------------------------
    def enemy_turn(self):
        self.enemy.update_effects()
        self.enemy.update_summons(self.player)
        self.enemy.regenerate()
        if self.enemy.is_defeated():
            self.update_labels()
            self.end_battle(True)
            return
        if not self.enemy.hand:
            self.enemy.refill_hand()
        if self.enemy.hand:
            idx = choose_card_index(self.enemy)
            if idx is None:
                self.enemy.discard_hand()
                self.log(f"{self.enemy.name} hesitates and redraws.")
            else:
                card = self.enemy.hand[idx]
                player_hp_before = self.player.hp
                self.enemy.play_card(idx, self.player)
                damage = max(0, player_hp_before - self.player.hp)
                msg = f"{self.enemy.name} plays {card.name}"
                if damage:
                    msg += f" dealing {damage} damage"
                self.log(msg)
        self.enemy.refill_hand()
        self.update_labels()
        if self.player.is_defeated():
            self.log("You lost the battle!")
            self.end_battle(False)
        else:
            self.new_turn()

    def end_player_turn(self):
        self.player.regenerate()
        self.update_labels()
        self.clear_action_frame()
        self.enemy_turn()

    def new_turn(self):
        self.clear_action_frame()
        self.player.update_effects()
        self.player.update_summons(self.enemy)
        self.player.regenerate()
        self.player.refill_hand()
        if self.player.is_defeated():
            self.update_labels()
            self.log("You lost the battle!")
            self.end_battle(False)
            return
        self.update_labels()
        self.show_hand()
        
    def start(self):
        self.player.refill_hand()
        self.enemy.refill_hand()
        self.new_turn()
        self.root.mainloop()
        self.root.destroy()

