# Medieval Duel: The Arcane Tournament

This repository contains a prototype for a card-based RPG duel engine written in Python. It provides the foundations for building a tournament-style game with magic-using characters, each with their own deck of cards.

## Features

- `Card` and `Character` classes representing the basic gameplay pieces.
- A simple battle engine where two characters play cards from a 4-card hand.
- Example damage and healing card effects.
- Console interface for testing battles.
- Tkinter-based start menu and GUI.
- Simple "Dungeon Battle" mode that displays placeholder sprites for a 1v1
  encounter.
- Deck building menu to select a character and construct a 20 card deck
  (maximum two copies of each card).

## Running the Demo

1. Ensure you have Python 3.8+ installed.
2. Install dependencies with `pip install -r requirements.txt`.
3. Run `python main.py` and choose a mode from the start menu.

This project is an early prototype and will expand to include additional characters, cards, and game modes.
The dungeon mode uses simple placeholder sprites generated at runtime with
`Pillow`. These sprites are created dynamically in memory, so no image assets
are stored in the repository.


## Data Modules

The repository now includes two data modules used by the engine:

- `bestiary.py` – definitions and lore for common dungeon enemies.
- `character_cards.py` – card libraries and lore for each playable character along with the universal card set.

These datasets are derived from the design codex and can be imported by other parts of the game for future content.

## Card Types and Leveling

Cards come in several rarity categories. **Common**, **Uncommon** and **Rare** cards can be found as loot or purchased from shops. **Unique** cards are tied to a particular character and are earned when that character levels up. The **Universal** card set is always available and can be used in any deck.

Characters gain experience after battles and level up when they reach their XP threshold. Leveling up increases their maximum stats based on the progression table and grants an unspent stat point. Future updates will use level ups to award unique cards.
