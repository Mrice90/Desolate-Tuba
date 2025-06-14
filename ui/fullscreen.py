import tkinter as tk

def create_fullscreen_root(title: str) -> tk.Tk:
    """Return a Tk root window configured for fullscreen."""
    root = tk.Tk()
    root.title(title)
    try:
        root.attributes("-fullscreen", True)
    except tk.TclError:
        root.state("zoomed")
    return root
