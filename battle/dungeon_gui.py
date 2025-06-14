import tkinter as tk
from tkinter import messagebox
import random
from PIL import Image, ImageDraw, ImageTk
from .gui import BattleGUI
from items import create_basic_items


class DungeonBattleGUI(BattleGUI):
    """BattleGUI variant that displays simple sprites on a canvas."""

    def _make_sprite(self, color: str, letter: str) -> Image:
        img = Image.new("RGBA", (60, 60), color)
        draw = ImageDraw.Draw(img)
        draw.text((20, 20), letter, fill="white")
        return img

    def __init__(self, player, enemy):
        super().__init__(player, enemy)
        self.root.title("Dungeon Battle")
        self.continue_dungeon = False

        self.canvas = tk.Canvas(self.root, width=400, height=300, bg="black")
        self.canvas.pack(before=self.log_text, fill="both", expand=True)

        self.player_img = ImageTk.PhotoImage(self._make_sprite("blue", "P"))
        self.enemy_img = ImageTk.PhotoImage(self._make_sprite("red", "E"))
        self.redraw()

    def redraw(self):
        self.canvas.delete("all")
        self.canvas.create_image(100, 220, image=self.player_img)
        enemy_x, enemy_y = 300, 80
        self.canvas.create_image(enemy_x, enemy_y, image=self.enemy_img)

        # Player summons
        px = 60
        for sm in self.player.summons:
            self.canvas.create_oval(px, 180, px + 20, 200, fill=sm.color)
            px += 25

        # Enemy summons
        ex = 340
        for sm in self.enemy.summons:
            self.canvas.create_oval(ex, 40, ex + 20, 60, fill=sm.color)
            ex += 25

        # Enemy health bar
        bar_width = 100
        bar_height = 10
        x0 = enemy_x - bar_width // 2
        y0 = enemy_y - 40
        hp_frac = max(0, self.enemy.hp) / self.enemy.max_hp
        # draw missing health in red background
        self.canvas.create_rectangle(x0, y0, x0 + bar_width, y0 + bar_height,
                                     fill="red", outline="white")
        # overlay current health in green
        self.canvas.create_rectangle(x0, y0, x0 + int(bar_width * hp_frac),
                                     y0 + bar_height, fill="green", outline="")
        # enemy name above bar
        self.canvas.create_text(enemy_x, y0 - 10, text=self.enemy.name,
                                fill="white")

    def update_labels(self):
        super().update_labels()
        self.redraw()

    def end_battle(self, won: bool):
        """Handle battle conclusion with loot and continue prompt."""
        self.victory = won
        if won:
            self.player.gain_xp(50)
            loot = None
            if random.random() < 0.5:
                loot = random.choice(create_basic_items())
                self.player.items.append(loot)
            msg = "You won the battle!"
            if loot:
                msg += f"\nYou found {loot.name}!"
            msg += "\nContinue to next battle?"
            self.continue_dungeon = messagebox.askyesno("Victory", msg)
        else:
            messagebox.showinfo("Defeat", "You lost the battle!")
            self.continue_dungeon = False
        self.root.quit()
