from pathlib import Path

# Define required directories and files
structure = {
    'cards': ['universal_cards.json'],
    'characters': ['characters.json'],
    'bestiary': ['bestiary.json'],
    'effects': ['status_effects.py'],
    'assets/art': ['.gitkeep'],
    'assets/music': ['.gitkeep'],
    'ui': ['__init__.py'],
    'modes': ['__init__.py'],
}

# Ensure main.py exists
Path('main.py').touch(exist_ok=True)

for folder, files in structure.items():
    dir_path = Path(folder)
    dir_path.mkdir(parents=True, exist_ok=True)
    for file in files:
        (dir_path / file).touch(exist_ok=True)
