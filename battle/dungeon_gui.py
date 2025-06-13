import tkinter as tk
from PIL import Image, ImageDraw, ImageTk
from .gui import BattleGUI


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

        self.canvas = tk.Canvas(self.root, width=400, height=300, bg="black")
        self.canvas.pack(before=self.log_text, fill="both", expand=True)

        self.player_img = ImageTk.PhotoImage(self._make_sprite("blue", "P"))
        self.enemy_img = ImageTk.PhotoImage(self._make_sprite("red", "E"))
        self.redraw()

    def redraw(self):
        self.canvas.delete("all")
        self.canvas.create_image(100, 220, image=self.player_img)
        self.canvas.create_image(300, 80, image=self.enemy_img)

    def update_labels(self):
        super().update_labels()
        self.redraw()
