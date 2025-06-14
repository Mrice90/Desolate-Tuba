from characters import Character
from cards import Card
from battle.engine import simple_damage

BESTIARY = [
    {
        "name": "Goblin",
        "description": "A small green-skinned humanoid known for mischief and ambush tactics. Goblins lurk in caves or forests, scavenging what they can; cunning but cowardly in nature.",
        "hp": 10,
        "damage": 3,
        "str": -1,
        "thaumaturgy": -1,
        "agi": 1,
        "res": -1,
        "tactics": "Often attacks in groups with crude weapons. Use area attacks or intimidation – they tend to flee if overwhelmed or if their leader falls."
    },
    {
        "name": "Orc",
        "description": "A brutish, muscular humanoid with greenish skin and protruding tusks. Orcs are tribal warriors living in caves or forts, valuing strength; they raid settlements and fight fiercely under warlords.",
        "hp": 30,
        "damage": 7,
        "str": 2,
        "thaumaturgy": -1,
        "agi": 0,
        "res": 1,
        "tactics": "Charges head-on using brute strength. Their high Strength means strong melee attacks – it's wise to weaken or outmaneuver them, and exploit their lower agility."
    },
    {
        "name": "Bandit",
        "description": "A rogue human or similar race turned to crime. Bandits prowl trade roads and ruins, looking to rob travelers. They are unscrupulous fighters, often lightly armored but crafty.",
        "hp": 20,
        "damage": 5,
        "str": 0,
        "thaumaturgy": -1,
        "agi": 1,
        "res": 0,
        "tactics": "Uses cheap shots and may feign surrender. They have average stats; try to intimidate or outsmart them. Blocking or parrying can negate their knife strikes."
    },
    {
        "name": "Cultist",
        "description": "A zealous follower of a dark religion or demon. Typically a robed human who has sacrificed sanity for forbidden magic. Frail in build but empowered by dark rituals.",
        "hp": 18,
        "damage": 6,
        "str": -1,
        "thaumaturgy": 2,
        "agi": 0,
        "res": -1,
        "tactics": "May cast dark spells (moderate Thaumaturgy) and summon minor demons. Disrupt their casting (stuns or silence) or strike quickly – they are physically weak and rely on magic."
    },
    {
        "name": "Skeleton",
        "description": "An animated human skeleton brought to unlife by necromancy. Mindless and relentless, often armed with rusted blades. Common guardians in dark dungeons or graveyards.",
        "hp": 10,
        "damage": 4,
        "str": 0,
        "thaumaturgy": -1,
        "agi": 0,
        "res": -1,
        "tactics": "Slow and fearless. Smash them with heavy attacks (blunt weapons excel) – their low Resilience means they crumble quickly, but they feel no pain so they won't stop unless destroyed."
    },
    {
        "name": "Zombie",
        "description": "A shambling corpse driven by undeath. Zombies are slow but tough, ignoring injuries that would fell a living being. Found in graveyards, crypts, or as necromancers’ minions.",
        "hp": 20,
        "damage": 5,
        "str": 0,
        "thaumaturgy": -1,
        "agi": -1,
        "res": 1,
        "tactics": "Lumbers forward and tries to grapple and bite. Their slow speed makes kiting effective. Aim for the head or use fire/holy damage to overcome their moderate resilience and regeneration."
    },
    {
        "name": "Ghost",
        "description": "The lingering spirit of a deceased being, translucent and floating. Ghosts haunt old battlefields or cursed sites. They are incorporeal, passing through walls and many attacks.",
        "hp": 15,
        "damage": 5,
        "str": -1,
        "thaumaturgy": 1,
        "agi": 2,
        "res": 0,
        "tactics": "Physical attacks pass through unless magically enhanced (requires Thaumaturgy). Use magic or enchanted weapons. They often inflict chill or fear – maintain courage and strike with spells or holy power."
    },
    {
        "name": "Lich",
        "description": "A powerful undead sorcerer who achieved immortality by binding its soul. Liches appear as skeletal mages in robes. They command other undead and guard ancient secrets.",
        "hp": 80,
        "damage": 15,
        "str": -1,
        "thaumaturgy": 4,
        "agi": 0,
        "res": 2,
        "tactics": "Casts devastating spells (high Thaumaturgy) and often has minions. Dispel its protective wards and avoid direct magical exchanges. Target its phylactery (soul vessel) if possible – otherwise, use overwhelming force but beware its life-draining magic."
    },
    {
        "name": "Slime",
        "description": "An amorphous blob creature often found in dank dungeons. It dissolves organic matter with acidic secretions. Colors vary (green, gray, etc.), indicating slight differences in toxicity.",
        "hp": 25,
        "damage": 6,
        "str": 0,
        "thaumaturgy": -1,
        "agi": -1,
        "res": 2,
        "tactics": "Resistant to physical attacks as they simply reform (high Resilience vs non-magic). Use fire or ice magic to solidify or evaporate it. Keep your distance to avoid its acid; ranged attacks work well."
    },
    {
        "name": "Giant Spider",
        "description": "A huge spider, often the size of a wolf or larger. Dwells in dark caves or forests, spinning large webs to trap prey. Often venomous and surprisingly intelligent in ambush tactics.",
        "hp": 22,
        "damage": 5,
        "str": 0,
        "thaumaturgy": -1,
        "agi": 2,
        "res": 0,
        "tactics": "It may shoot webs to entangle or drop from above. High Agility makes it evasive – area attacks or fire can clear webs and hit it. Avoid its poisonous bite (carry antidotes or use ranged attacks to stay clear)."
    },
    {
        "name": "Giant Bat",
        "description": "An oversized bat with a wingspan several feet across. Roosts in dark caves or ruins. Hunts in packs, using echolocation squeaks that disorient prey in darkness.",
        "hp": 15,
        "damage": 4,
        "str": -1,
        "thaumaturgy": -1,
        "agi": 3,
        "res": -1,
        "tactics": "They swoop quickly (very high Agility) but are fragile. Loud noise or bright light can disrupt them. Use ranged attacks when they dive, or defensive stances to avoid being knocked down by their swooping strikes."
    },
    {
        "name": "Dire Wolf",
        "description": "A large, ferocious wolf roughly twice the size of a normal wolf. Often found in packs in wilderness or as monster pets. Strong jaws and relentless pack tactics make them deadly.",
        "hp": 28,
        "damage": 7,
        "str": 1,
        "thaumaturgy": -1,
        "agi": 2,
        "res": 0,
        "tactics": "Often attacks with pack mates, trying to flank. Their Agility and Strength are high – try to isolate them. Fire or loud noise can spook them; otherwise, focus one wolf at a time to thin the pack."
    },
    {
        "name": "Troll",
        "description": "A hulking regenerative brute found under bridges or in caves. Trolls have long arms, warty skin, and can recover from wounds rapidly. Dull-witted but extremely strong.",
        "hp": 50,
        "damage": 12,
        "str": 2,
        "thaumaturgy": -1,
        "agi": -1,
        "res": 2,
        "tactics": "Its regeneration lets it recover unless wounds are cauterized (use fire or acid to stop healing). Keep your distance to avoid its powerful melee swings. Exploit its low Agility by outmaneuvering it – trolls can be easily outrun or distracted."
    },
    {
        "name": "Golem",
        "description": "An animated construct of stone or clay, created by magic to guard or labor. Golems are emotionless and follow their creator’s commands. Extremely durable and strong, but slow.",
        "hp": 60,
        "damage": 10,
        "str": 3,
        "thaumaturgy": 0,
        "agi": -1,
        "res": 3,
        "tactics": "High Resilience means most attacks barely scratch it – use magic or target any runes or control talismans on its body. It’s slow (low Agility), so kite it or use hit-and-run tactics. Avoid its heavy blows, as they can crush armor."
    },
    {
        "name": "Fire Elemental",
        "description": "A being of living flame summoned from the Plane of Fire. It appears as a vaguely humanoid blaze. It burns anything it touches and radiates intense heat.",
        "hp": 30,
        "damage": 8,
        "str": 0,
        "thaumaturgy": 2,
        "agi": 0,
        "res": 1,
        "tactics": "Resistant or immune to fire – don’t bother with flame attacks. Use cold or water-based abilities to weaken it. It will try to immolate close-range fighters, so attack from afar if possible. Dispel magic can banish it since it’s a summoned being."
    },
    {
        "name": "Ice Elemental",
        "description": "A frigid elemental from the Plane of Water/Ice. Appears as a hulking form of ice and frost. It drains heat from surroundings, freezing anything nearby.",
        "hp": 35,
        "damage": 8,
        "str": 0,
        "thaumaturgy": 2,
        "agi": -1,
        "res": 2,
        "tactics": "Vulnerable to fire and high impact (shattering blows). Its icy aura can slow you (lower your Agility), so counter with fire or keep distance. Avoid being frozen by its attacks – break line of sight if you start slowing down, and chip away at it with blunt weapons or flames."
    },
    {
        "name": "Demon",
        "description": "A generic term for a powerful fiend from a hellish realm. This demon stands taller than a man, with claws, horns, and often wielding hellish magic. Violent and cunning.",
        "hp": 50,
        "damage": 14,
        "str": 3,
        "thaumaturgy": 1,
        "agi": 0,
        "res": 2,
        "tactics": "Combines brute strength and dark magic – expect both physical and magical attacks. Use holy or radiant damage if available. Keep moving to avoid its melee while disrupting its spellcasting when you can. Focus damage on it quickly; do not let it prolong the fight as some demons grow more frenzied when injured."
    },
    {
        "name": "Imp",
        "description": "A small lesser demon the size of a cat or goblin. Imps have wings and tails, and delight in causing trouble with minor fire or shadow spells. Often serve stronger demons or warlocks.",
        "hp": 8,
        "damage": 4,
        "str": -1,
        "thaumaturgy": 0,
        "agi": 2,
        "res": -1,
        "tactics": "Annoying but frail. They fly around and cast little fireballs – use quick ranged attacks or swat them down with area spells. One solid hit will usually dispatch an imp. They may try to flee if alone or outmatched."
    },
    {
        "name": "Dragon",
        "description": "A young dragon, large and armored in scales. Dragons are intelligent, winged reptiles that breathe fire (or other elements). Even a young dragon is a deadly foe with fiery breath and razor claws.",
        "hp": 100,
        "damage": 18,
        "str": 4,
        "thaumaturgy": 2,
        "agi": 1,
        "res": 3,
        "tactics": "Highly dangerous: can attack in melee and at range (breath weapon). Its scales grant high Resilience, so target softer spots (underbelly) or use magic. Avoid clustering your party to minimize the effect of its breath. Exploit its pride – a dragon can sometimes be baited into a trap or striking prematurely."
    },
    {
        "name": "Harpy",
        "description": "A vicious creature with the body of a bird and the head of a human female. Harpies nest on cliffs and lure prey with eerie songs. They swoop to attack with talons once prey is entranced or distracted.",
        "hp": 16,
        "damage": 5,
        "str": 0,
        "thaumaturgy": -1,
        "agi": 1,
        "res": -1,
        "tactics": "They often try to charm or distract targets with their song – cover your ears or break the effect with pain or loud noise. In combat, they rely on flight; use ranged weapons or magic to knock them out of the sky. They are relatively fragile if you land a solid hit."
    },
    {
        "name": "Minotaur",
        "description": "A bull-headed humanoid of great size and strength. Minotaurs dwell in labyrinths or underground ruins, often as solitary guardians. They wield heavy axes or clubs and charge their prey with horned heads.",
        "hp": 55,
        "damage": 12,
        "str": 3,
        "thaumaturgy": -1,
        "agi": 0,
        "res": 2,
        "tactics": "It will charge in straight lines – sidestep their charge to avoid massive damage (use environment pillars to your advantage). High Strength makes their blows deadly, but they aren’t too agile. Wear them down with ranged attacks or coordinated strikes, and avoid getting cornered in its labyrinthine lair."
    },
    {
        "name": "Basilisk",
        "description": "A monstrous reptile with eight legs and a crest, known for its petrifying gaze. Found in deep caves or jungles. Its eyes can turn creatures to stone, and it has a venomous bite.",
        "hp": 40,
        "damage": 9,
        "str": 1,
        "thaumaturgy": 1,
        "agi": 0,
        "res": 1,
        "tactics": "Never meet its gaze – use mirrors or fight blindfolded (relying on other senses) to avoid petrification. Strike from the sides or use ranged attacks. Its scaly hide offers decent defense, but enough force or magic will bring it down. If bitten, cure the poison quickly as it can weaken you severely."
    },
    {
        "name": "Mimic",
        "description": "A shapeshifting dungeon predator that often takes the form of a treasure chest or door. When prey comes close, it reveals a mass of teeth and a sticky tongue to snare victims.",
        "hp": 35,
        "damage": 8,
        "str": 1,
        "thaumaturgy": -1,
        "agi": 0,
        "res": 2,
        "tactics": "Approach loot with caution – test suspicious chests with a pole or small item. Once a mimic reveals itself, avoid its adhesive tongue by keeping distance or severing it. They have high Resilience (thick hide) but fire can burn their flesh. After a mimic attacks, it can’t move quickly – use that window to strike hard."
    },
    {
        "name": "Dark Knight",
        "description": "A fallen or undead knight clad in blackened armor. This warrior retains martial skill, fighting with sword and shield (and possibly dark magic). Often guards cursed keeps or serves evil lords.",
        "hp": 45,
        "damage": 10,
        "str": 2,
        "thaumaturgy": 0,
        "agi": 0,
        "res": 2,
        "tactics": "Well-armored (high Resilience) and skilled, it blocks frontal attacks with its shield. Use magic or flanking maneuvers. If undead (Death Knight), holy magic will weaken it. It may use tainted auras or life-drain attacks, so don’t engage it in a prolonged duel – strike decisively when its guard is down."
    },
    {
        "name": "Vampire",
        "description": "An undead creature of the night, often of noble bearing. Vampires appear human but with fangs and pale complexion. They feed on blood and can charm victims. Strong, fast, and capable of regenerating from wounds.",
        "hp": 48,
        "damage": 11,
        "str": 2,
        "thaumaturgy": 1,
        "agi": 2,
        "res": 2,
        "tactics": "They avoid sunlight and use charm or stealth to weaken targets before feeding. Use holy symbols or sunlight to drive them back. Decapitation or stakes through the heart can prevent them from regenerating."
    },
    {
        "name": "Earth Elemental",
        "description": "A boulder-like elemental from the Plane of Earth. It is incredibly strong and tough but very slow.",
        "hp": 50,
        "damage": 10,
        "str": 3,
        "thaumaturgy": 1,
        "agi": -2,
        "res": 3,
        "tactics": "Use magic or elemental countermeasures to break its stony body. Stay mobile and avoid its heavy blows."
    },
    {
        "name": "Air Elemental",
        "description": "A swirling vortex of air given sentience. Hard to hit and strikes with razor wind.",
        "hp": 20,
        "damage": 6,
        "str": -1,
        "thaumaturgy": 2,
        "agi": 3,
        "res": -1,
        "tactics": "Magical attacks work best. Anchor yourself so it cannot toss you around with gusts of wind."
    },
    {
        "name": "Hydra",
        "description": "A multi-headed reptilian beast. Severed heads regrow unless burned.",
        "hp": 60,
        "damage": 12,
        "str": 2,
        "thaumaturgy": -1,
        "agi": 0,
        "res": 2,
        "tactics": "Use fire to cauterize necks after cutting heads off, or the creature becomes even more dangerous."
    },
    {
        "name": "Werewolf",
        "description": "A cursed human that transforms into a wolf-like beast with ferocious claws and regenerative abilities.",
        "hp": 35,
        "damage": 9,
        "str": 2,
        "thaumaturgy": 0,
        "agi": 2,
        "res": 1,
        "tactics": "Silver weapons or fire suppress its regeneration. Avoid fighting it alone at night."
    },
    {
        "name": "Banshee",
        "description": "A malevolent female spirit whose mournful wail can incapacitate or kill.",
        "hp": 20,
        "damage": 10,
        "str": -1,
        "thaumaturgy": 3,
        "agi": 1,
        "res": 0,
        "tactics": "Use ear protection or silence magic to counter her wail. Holy magic disrupts her form."
    }
]

# automatically assign an experience reward if missing
for _idx, _beast in enumerate(BESTIARY, start=1):
    if 'xp' not in _beast:
        _beast['xp'] = _beast['hp'] + _beast['damage'] * 2


def create_enemy_for_level(level: int) -> Character:
    """Return a simple enemy ``Character`` from ``BESTIARY`` scaled by level."""
    idx = min(len(BESTIARY) - 1, max(0, level // 2))
    data = BESTIARY[idx]

    attack = Card(
        "Attack",
        cost=1,
        resource_type="stamina",
        effect_function=lambda u, t, dmg=data["damage"]: simple_damage(u, t, dmg),
        description=f"Deal {data['damage']} damage."
    )

    enemy = Character(
        name=data["name"],
        hp=data["hp"],
        mana=0,
        stamina=5,
        deck=[attack] * 5,
        level=level,
        strength_mod=data.get("str", 0),
        thaumaturgy_mod=data.get("thaumaturgy", 0),
        agility_mod=data.get("agi", 0),
        resilience_mod=data.get("res", 0),
    )
    enemy.xp_reward = data.get("xp", 50)
    return enemy
