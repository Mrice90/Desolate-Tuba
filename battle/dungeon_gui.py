import tkinter as tk
from PIL import Image, ImageDraw, ImageTk
from tkinter import messagebox


class DungeonBattleGUI:
    """Simple 1v1 battle view with placeholder sprites."""

    def _make_sprite(self, color: str, letter: str) -> Image:
        """Create a simple square sprite of a given color with a single letter."""
        img = Image.new("RGBA", (60, 60), color)
        draw = ImageDraw.Draw(img)
        draw.text((20, 20), letter, fill="white")
        return img

    def __init__(self, player, enemy):
        self.player = player
        self.enemy = enemy
        self.root = tk.Tk()
        self.root.title("Dungeon Battle")

        # Canvas for drawing sprites and effects
        self.canvas = tk.Canvas(self.root, width=400, height=300, bg="black")
        self.canvas.pack(fill="both", expand=True)

        # Create simple placeholder sprites in memory
        self.player_img = ImageTk.PhotoImage(self._make_sprite("blue", "P"))
        self.enemy_img = ImageTk.PhotoImage(self._make_sprite("red", "E"))

        # Status labels
        self.info = tk.Label(self.root, text="")
        self.info.pack()

        # Action buttons
        self.btn_frame = tk.Frame(self.root)
        self.btn_frame.pack(fill="x")
        self.attack_btn = tk.Button(self.btn_frame, text="Attack", command=self.attack)
        self.attack_btn.pack(side="left", expand=True, fill="x")
        self.flee_btn = tk.Button(self.btn_frame, text="Flee", command=self.flee)
        self.flee_btn.pack(side="left", expand=True, fill="x")

        self.redraw()

    def redraw(self):
        self.canvas.delete("all")
        # player in foreground left-bottom
        self.canvas.create_image(100, 220, image=self.player_img)
        # enemy in background right-top
        self.canvas.create_image(300, 80, image=self.enemy_img)
        # update stats
        text = f"{self.player.name} HP:{self.player.hp}/{self.player.max_hp}  VS  {self.enemy.name} HP:{self.enemy.hp}/{self.enemy.max_hp}"
        self.info.config(text=text)

    def attack(self):
        # simple effect: flash rectangle on enemy
        rect = self.canvas.create_rectangle(260, 40, 340, 120, fill="yellow")
        self.canvas.after(200, lambda: self.canvas.delete(rect))
        self.enemy.hp -= 3
        if self.enemy.hp <= 0:
            messagebox.showinfo("Victory", "Enemy defeated!")
            self.player.gain_xp(20)
            self.root.quit()
        else:
            self.root.after(300, self.enemy_attack)
        self.redraw()

    def enemy_attack(self):
        rect = self.canvas.create_rectangle(60, 180, 140, 260, fill="red")
        self.canvas.after(200, lambda: self.canvas.delete(rect))
        self.player.hp -= 2
        if self.player.hp <= 0:
            messagebox.showinfo("Defeat", "You were defeated!")
            self.root.quit()
        self.redraw()

    def flee(self):
        messagebox.showinfo("Flee", "You fled the battle!")
        self.root.quit()

    def start(self):
        self.player.refill_hand()
        self.enemy.refill_hand()
        self.root.mainloop()
