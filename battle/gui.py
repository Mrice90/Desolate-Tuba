import tkinter as tk
from tkinter import messagebox


class BattleGUI:
    """Simple Tkinter interface for the duel engine."""

    def __init__(self, player, enemy):
        self.player = player
        self.enemy = enemy
        self.root = tk.Tk()
        self.root.title("Medieval Duel")

        # Log box
        self.log_text = tk.Text(self.root, height=10, state="disabled")
        self.log_text.pack(fill="both", expand=True)

        # Enemy and player status labels
        self.enemy_label = tk.Label(self.root, text="")
        self.enemy_label.pack()
        self.player_label = tk.Label(self.root, text="")
        self.player_label.pack()

        self.card_buttons = []

    def _stats(self, character):
        return f"{character.name} HP:{character.hp} Mana:{character.mana} Stamina:{character.stamina}"

    def log(self, msg):
        self.log_text.configure(state="normal")
        self.log_text.insert("end", msg + "\n")
        self.log_text.configure(state="disabled")
        self.log_text.see("end")

    def update_labels(self):
        self.enemy_label.configure(text=self._stats(self.enemy))
        self.player_label.configure(text=self._stats(self.player))

    def update_hand(self):
        for btn in self.card_buttons:
            btn.destroy()
        self.card_buttons = []
        for idx, card in enumerate(self.player.hand):
            text = f"{card.name} ({card.cost} {card.resource_type})"
            btn = tk.Button(self.root, text=text, command=lambda i=idx: self.play_card(i))
            btn.pack(fill="x")
            self.card_buttons.append(btn)

    def play_card(self, index):
        card = self.player.hand[index]
        if not self.player.play_card(index, self.enemy):
            self.log("Failed to play card!")
            return
        self.log(f"You play {card.name}")
        if self.enemy.is_defeated():
            self.update_labels()
            self.log("You won the battle!")
            messagebox.showinfo("Victory", "You won the battle!")
            self.root.quit()
            return
        # Enemy turn - plays first card
        self.enemy.play_card(0, self.player)
        self.enemy.refill_hand()
        if self.player.is_defeated():
            self.update_labels()
            self.log("You lost the battle!")
            messagebox.showinfo("Defeat", "You lost the battle!")
            self.root.quit()
            return
        self.update_labels()
        self.update_hand()

    def start(self):
        self.player.refill_hand()
        self.enemy.refill_hand()
        self.update_labels()
        self.update_hand()
        self.root.mainloop()

