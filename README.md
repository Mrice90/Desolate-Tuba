# Medieval Duel: The Arcane Tournament

This repository contains a prototype for a card-based RPG duel engine written in Python. It provides the foundations for building a tournament-style game with magic-using characters, each with their own deck of cards.

## Features

- `Card` and `Character` classes representing the basic gameplay pieces.
- A simple battle engine where two characters play cards from a 4-card hand.
- Example damage and healing card effects.
- Console interface for testing battles.
- Optional Tkinter-based GUI.

## Running the Demo

1. Ensure you have Python 3.8+ installed.
2. Install dependencies with `pip install -r requirements.txt`.
3. Run `python main.py` to start a sample duel in the console.
4. Run `python main.py --gui` to launch the graphical interface with a start menu.

This project is an early prototype and will expand to include additional characters, cards, and game modes.

## Citations

- [Pygame](https://www.pygame.org/) is used for the start menu. It is distributed under the LGPL.
- Placeholder art assets are expected from the [Kenney.nl](https://kenney.nl/assets) collections which are released under the CC0 license.
