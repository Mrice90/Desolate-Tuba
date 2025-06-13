import tkinter as tk
from tkinter import messagebox


class BattleGUI:
    """Graphical battle interface with quadrant command layout."""

    def __init__(self, player, enemy):
        self.player = player
        self.enemy = enemy
        self.root = tk.Tk()
        self.root.title("Medieval Duel")

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

        self.battle_btn = tk.Button(self.option_frame, text="Battle",
                                    command=self.show_hand)
        self.flee_btn = tk.Button(self.option_frame, text="Flee",
                                  command=self.flee)
        self.items_btn = tk.Button(self.option_frame, text="Items",
                                   command=self.show_items)
        self.fold_btn = tk.Button(self.option_frame, text="Fold",
                                  command=self.fold)

        self.battle_btn.grid(row=0, column=0, sticky="nsew", padx=5, pady=5)
        self.flee_btn.grid(row=0, column=1, sticky="nsew", padx=5, pady=5)
        self.items_btn.grid(row=1, column=0, sticky="nsew", padx=5, pady=5)
        self.fold_btn.grid(row=1, column=1, sticky="nsew", padx=5, pady=5)

        for i in range(2):
            self.option_frame.rowconfigure(i, weight=1)
            self.option_frame.columnconfigure(i, weight=1)

        self.card_buttons = []
        self.item_buttons = []

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
            text = f"{card.name} ({card.cost} {card.resource_type})"
            btn = tk.Button(self.action_frame, text=text,
                            command=lambda i=idx: self.play_card(i))
            btn.pack(fill="x")
            self.card_buttons.append(btn)
        tk.Button(self.action_frame, text="Back",
                  command=self.clear_action_frame).pack(fill="x")

    def show_items(self):
        self.clear_action_frame()
        if not self.player.items:
            tk.Label(self.action_frame, text="No items available").pack()
            return
        for idx, item in enumerate(self.player.items):
            text = item.name
            btn = tk.Button(self.action_frame, text=text,
                            command=lambda i=idx: self.use_item(i))
            btn.pack(fill="x")
            self.item_buttons.append(btn)
        tk.Button(self.action_frame, text="Back",
                  command=self.clear_action_frame).pack(fill="x")

    def flee(self):
        self.log("You fled the battle!")
        messagebox.showinfo("Flee", "You fled the battle!")
        self.root.quit()

    def fold(self):
        self.player.discard_hand()
        self.log("You fold and redraw your hand.")
        self.end_player_turn()

    def play_card(self, index):
        card = self.player.hand[index]
        if not self.player.play_card(index, self.enemy):
            self.log("Failed to play card!")
            return
        self.log(f"You play {card.name}")
        self.update_labels()
        if self.enemy.is_defeated():
            self.log("You won the battle!")
            messagebox.showinfo("Victory", "You won the battle!")
            self.player.gain_xp(50)
            self.root.quit()
            return
        self.end_player_turn()

    def use_item(self, index):
        if not self.player.use_item(index):
            return
        self.log(f"You use {self.player.name}'s item")
        self.update_labels()
        self.end_player_turn()

    # Turn management -----------------------------------------------------
    def enemy_turn(self):
        self.enemy.update_effects()
        self.enemy.regenerate()
        if self.enemy.is_defeated():
            self.update_labels()
            self.log("You won the battle!")
            messagebox.showinfo("Victory", "You won the battle!")
            self.player.gain_xp(50)
            self.root.quit()
            return
        if not self.enemy.hand:
            self.enemy.refill_hand()
        if self.enemy.hand:
            card = self.enemy.hand[0]
            self.enemy.play_card(0, self.player)
            self.log(f"Enemy plays {card.name}")
        self.enemy.refill_hand()
        self.update_labels()
        if self.player.is_defeated():
            self.log("You lost the battle!")
            messagebox.showinfo("Defeat", "You lost the battle!")
            self.root.quit()
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
        self.player.regenerate()
        self.player.refill_hand()
        if self.player.is_defeated():
            self.update_labels()
            self.log("You lost the battle!")
            messagebox.showinfo("Defeat", "You lost the battle!")
            self.root.quit()
            return
        self.update_labels()
        self.show_hand()
        
    def start(self):
        self.player.refill_hand()
        self.enemy.refill_hand()
        self.new_turn()
        self.root.mainloop()

