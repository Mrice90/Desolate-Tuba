CHARACTER_CARDS = {
    "Aurelia Flameheart": {
        "class": "Pyromancer",
        "lore": "A fiery sorceress from the Emberpeak clan, Aurelia learned to command fire from a young age. Passionate and headstrong, she channels the fury of flames in battle. Her village’s destruction by frost trolls ignited her quest to master fire magic – both to protect and to exact vengeance. Now she wanders, a blazing beacon of hope or destruction depending on those who cross her.",
        "cards": [
            {"name": "Firebolt", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Launch a fast bolt of fire at a single target, dealing moderate fire damage.", "star": "Damage scales with Thaumaturgy (magic power)"},
            {"name": "Flame Wave", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Unleash a wave of flames that hits multiple enemies in front of the caster.", "star": "Damage increases with Thaumaturgy; enemies with lower Resilience take more damage"},
            {"name": "Ignite", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Ignite an enemy, causing initial fire damage and additional burning damage over a short time.", "star": "Burning damage over time is enhanced by Thaumaturgy"},
            {"name": "Inferno", "type": "Damage", "cost": 5, "resource": "Mana", "effect": "Summon an inferno that engulfs all enemies, dealing heavy area fire damage.", "star": "Higher Thaumaturgy greatly amplifies the area damage"},
            {"name": "Summon Flame Imp", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Summon a small flame imp to fight for the caster. The imp deals minor fire damage to enemies.", "star": "Imp's attack power is boosted by caster’s Thaumaturgy"},
            {"name": "Summon Fire Elemental", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a fire elemental with high health to assist in battle. The elemental attacks with fire and can absorb some damage.", "star": "Elemental’s health and damage scale with Thaumaturgy"},
            {"name": "Summon Phoenix", "type": "Summon", "cost": 6, "resource": "Mana", "effect": "Summon a phoenix that attacks enemies and can revive itself once from its ashes, dealing area fire damage upon revival.", "star": "Phoenix’s attack is influenced by Thaumaturgy; its revival burst scales with caster’s Thaumaturgy"},
            {"name": "Firewall", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Conjure a wall of fire on the field. Enemies that pass through it take fire damage.", "star": "Damage from the wall increases with Thaumaturgy; wall size/duration may extend with Resilience"},
            {"name": "Flame Shield", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Envelop yourself or an ally in a shield of flames, reducing incoming damage and burning melee attackers for a short duration.", "star": "Shield strength scales with Resilience; burn damage scales with Thaumaturgy"},
            {"name": "Inner Fire", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Ignite inner flames to boost the target’s attack power. Increases Strength for a few turns.", "star": "Buff amount equals a portion of caster’s Thaumaturgy (enhancing Strength)"},
            {"name": "Blazing Speed", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "A burst of fiery energy increases the target’s Agility, allowing them to move faster and dodge more easily.", "star": "Agility boost is influenced by caster’s Thaumaturgy"},
            {"name": "Phoenix Flame", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Imbue an ally with the essence of a phoenix, granting regeneration (heal over time) and a chance to cheat death once during the buff duration.", "star": "Regeneration rate increases with caster’s Resilience; revival effect triggers once regardless of stats"},
            {"name": "Burning Weakness", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Curse an enemy with searing pain, reducing their Strength (attack power) due to burning agony.", "star": "Strength reduction potency increases with caster’s Thaumaturgy"},
            {"name": "Smoke Cloud", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Summon thick smoke around an enemy, impairing their vision. Lowers the target’s Agility and accuracy.", "star": "Higher Thaumaturgy makes the smoke more incapacitating (greater Agility drop)"},
            {"name": "Melting Armor", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Superheat the target’s armor, making it brittle. Reduces the target’s Resilience.", "star": "Reduction in Resilience is more severe with higher Thaumaturgy"},
            {"name": "Searing Mark", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Brand an enemy with a fiery mark. Marked target takes extra damage from all sources for a short duration.", "star": "Damage amplification on the target scales with caster’s Thaumaturgy"},
            {"name": "Flame Step", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Teleport in a burst of flame to a visible location. Leaves a small fire at the starting point.", "star": "Teleport range slightly increases with Agility; flame left behind damage scales with Thaumaturgy"},
            {"name": "Cauterize", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Use controlled flame to seal wounds. Restore a small amount of health to yourself or an ally (stops bleeding).", "star": "Healing amount scales with Resilience (representing vitality)"},
            {"name": "Fire Trap", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Lay a hidden fiery trap on the ground. When an enemy steps on it, it erupts, dealing fire damage.", "star": "Trap damage scales with Thaumaturgy; higher Agility increases chance enemies trigger it"},
            {"name": "Flare", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Launch a bright flare into the air, illuminating dark areas and revealing invisible or hidden enemies for a short time.", "star": "No direct stat influence (effectiveness is situational, non-scaling)"}
        ]
    },
    "Darius Nightshade": {
        "class": "Necromancer",
        "lore": "Once a scholar of the arcane, Darius turned to forbidden necromancy after tragedy struck his family. Brooding and intelligent, he now walks a dark path, conversing with spirits and raising the dead to do his bidding. Despite his grim demeanor, a spark of his old compassion remains, hinting he might seek redemption even as he delves into deathly arts.",
        "cards": [
            {"name": "Shadow Bolt", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Hurl a bolt of dark energy at an enemy, dealing moderate shadow damage.", "star": "Damage scales with Thaumaturgy"},
            {"name": "Life Drain", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Drain the life force of a target, dealing damage and healing the caster for a portion of the damage dealt.", "star": "Damage and self-heal amount scale with Thaumaturgy; healing also influenced by caster’s Resilience"},
            {"name": "Bone Spear", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Summon a sharp bone projectile that pierces through enemies in a line, dealing physical piercing damage.", "star": "Damage increases with Thaumaturgy; ignores some defenses due to piercing (Strength aids physical penetration)"},
            {"name": "Corpse Explosion", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Cause a corpse on the field to explode in dark energy, dealing area damage to all nearby enemies.", "star": "Damage scales with Thaumaturgy; larger effect radius if target had high Strength"},
            {"name": "Raise Skeletons", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Raise a pair of skeletal warriors from nearby corpses to fight for you. Skeletons are weak individually but can swarm enemies.", "star": "Skeleton attack power scales with caster’s Strength; number of skeletons fixed"},
            {"name": "Summon Ghoul", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon a ghoul – a resilient undead that claws at enemies and can absorb moderate damage.", "star": "Ghoul’s health scales with Resilience; damage scales with Strength"},
            {"name": "Summon Wraith", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Call forth a wraith that floats through terrain. It deals cold necrotic damage and can frighten enemies.", "star": "Wraith’s damage scales with Thaumaturgy; its fright effect is harder to resist with high caster Thaumaturgy"},
            {"name": "Bone Wall", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Erect a wall of interlocking bones at a target location. Acts as cover and obstacle until destroyed.", "star": "Bone Wall durability scales with Resilience; length of wall slightly increases with Thaumaturgy"},
            {"name": "Dark Pact", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Sacrifice a bit of your own life force to empower your magic. Increases your Thaumaturgy for a few turns at the cost of some HP.", "star": "Thaumaturgy increase is fixed; HP sacrifice amount reduces with higher Resilience"},
            {"name": "Unholy Vigour", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Imbue an undead ally with dark energy, boosting its Strength and speed.", "star": "Buff strength scales with caster’s Thaumaturgy; Agility boost also scales with Thaumaturgy"},
            {"name": "Bone Armor", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Encase yourself or an ally in protective bone fragments. Increases Resilience.", "star": "Resilience buff scales with caster’s Resilience and Thaumaturgy"},
            {"name": "Vampiric Aura", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Surround yourself with a vampiric aura for a short time. Allies (and you) heal for a portion of damage dealt to enemies.", "star": "Life steal percentage is influenced by Thaumaturgy; healing received also influenced by Resilience"},
            {"name": "Curse of Frailty", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Curse a target, weakening their body. Lowers the target’s Strength and physical damage output.", "star": "Strength reduction scales with Thaumaturgy"},
            {"name": "Fear", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Fill an enemy’s mind with terror. The target is frightened, possibly skipping its turn or acting inefficiently.", "star": "Harder to resist with high caster Thaumaturgy"},
            {"name": "Wither", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Accelerate the decay in an enemy, reducing their Resilience as their body or armor deteriorates.", "star": "Resilience reduction scales with Thaumaturgy"},
            {"name": "Cripple", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Invoke a necrotic curse on an enemy’s limbs, reducing their Agility (slowing movement and attack speed).", "star": "Agility reduction potency increases with Thaumaturgy"},
            {"name": "Grave Step", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Teleport short distances by vanishing into shadows or stepping through a nearby corpse’s space.", "star": "Teleport range increases slightly with Agility"},
            {"name": "Soul Tap", "type": "Utility", "cost": 0, "resource": "Mana", "effect": "Draw ambient soul energy or from a corpse to restore some of your mana.", "star": "Mana restored scales with Thaumaturgy"},
            {"name": "Death Shroud", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Wrap yourself in a shroud of darkness, becoming ethereal for one turn. You avoid all physical damage during this time.", "star": "Duration is fixed (1 turn)"},
            {"name": "Whispering Dead", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Consult the whispers of spirits. Gain insight into upcoming events or draw 2 cards from your deck.", "star": "No direct stat influence"}
        ]
    },
    "Thorne Oakenshade": {
        "class": "Druid",
        "lore": "Thorne is a guardian of the wilds, a druid born under ancient oaks. Raised by a circle of elders deep in the forest, he speaks for the trees and beasts. Calm yet feral when provoked, he uses nature’s fury to defend against civilization’s encroachment. His lore of herbs and animals is unmatched, and many travelers owe their lives to his healing and guidance under the canopy.",
        "cards": [
            {"name": "Thorn Whip", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Lash an enemy with a magical thorned vine, dealing physical nature damage and possibly slowing the target.", "star": "Damage scales with Strength; slow effect intensity scales with Thaumaturgy"},
            {"name": "Bramble Burst", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Cause thorny brambles to erupt from the ground in an area, damaging all enemies nearby.", "star": "Damage increases with Thaumaturgy; higher Strength marginally increases area size"},
            {"name": "Feral Strike", "type": "Damage", "cost": 2, "resource": "Stamina", "effect": "Briefly channel beastly strength to claw or strike an enemy, dealing heavy melee damage.", "star": "Damage scales with Strength; minor lifesteal effect if Resilience is high"},
            {"name": "Toxic Spores", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Release poisonous spores at a target area, dealing initial damage and poisoning enemies for additional damage over time.", "star": "Initial and damage-over-time scale with Thaumaturgy; poison duration influenced by caster’s Resilience"},
            {"name": "Summon Wolf", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Summon a wolf companion to fight by your side. The wolf is fast and excels at single-target damage.", "star": "Wolf’s attack scales with Strength; its speed scales with Agility"},
            {"name": "Summon Bear", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon a bear ally. The bear has high HP and strong attack, serving as a durable frontline fighter.", "star": "Bear’s health and damage scale with Strength; its defense benefits from caster’s Resilience"},
            {"name": "Summon Treant", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a mighty treant. The treant can entangle enemies and absorb a lot of damage.", "star": "Treant’s HP scales with Resilience; its entangling attack strength scales with Thaumaturgy"},
            {"name": "Vine Wall", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Grow a wall of thick vines at a target location. Creates cover and impedes enemy movement until destroyed or withered.", "star": "Vine Wall durability scales with Resilience; length/area covered increases with Thaumaturgy"},
            {"name": "Barkskin", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Toughen the target’s skin like bark, increasing their Resilience and reducing physical damage taken.", "star": "Defense bonus scales with caster’s Resilience"},
            {"name": "Wild Strength", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Infuse an ally with primal strength, increasing their Strength and melee damage for a few turns.", "star": "Strength increase scales with caster’s Thaumaturgy"},
            {"name": "Nature’s Grace", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Bless an ally with the grace of nature, enhancing their Agility for a short duration.", "star": "Agility increase scales with Thaumaturgy"},
            {"name": "Bear Form", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Shapeshift yourself into a bear for a short time, greatly increasing Strength and Resilience but limiting spellcasting.", "star": "Stat boosts are significant and fixed; duration slightly extends with higher Resilience"},
            {"name": "Entangle", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Roots burst from the ground to immobilize an enemy. The target cannot move and has reduced Agility while entangled.", "star": "Harder to escape if caster’s Thaumaturgy is high; Agility reduction is fixed"},
            {"name": "Poison Thorns", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Cause painful poisoned thorns to pierce an enemy, reducing their Strength and inflicting a mild poison damage over time.", "star": "Strength reduction and poison damage scale with Thaumaturgy"},
            {"name": "Mark of Prey", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Magically mark an enemy as prey. Your summoned creatures deal extra damage to the marked target.", "star": "Damage bonus from summons scales with caster’s Thaumaturgy"},
            {"name": "Erosion", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Accelerate natural decay on the target’s armor and defenses, lowering their Resilience.", "star": "Resilience reduction scales with Thaumaturgy"},
            {"name": "Healing Herbs", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Use natural herbs and druidic magic to heal a target for a moderate amount of HP.", "star": "Healing amount scales with Resilience and Thaumaturgy"},
            {"name": "Purify", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Cleanse a target of poisons or venom and remove minor debuffs, using natural remedies.", "star": "No direct stat influence"},
            {"name": "Rejuvenation", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Channel nature’s energy to restore some mana and stamina to yourself over a short time.", "star": "Resource restoration amount scales with Thaumaturgy"},
            {"name": "Nature’s Path", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Imbue an ally with knowledge of the wild. Allows them to move through difficult terrain freely and increases movement range this turn.", "star": "No direct stat influence"}
        ]
    },
    "Seraphina Dawnshield": {
        "class": "Cleric of Light",
        "lore": "Seraphina is a devoted cleric knight of the Radiant Order, carrying both a sword and the blessings of the sun god. She was orphaned by undead as a child but saved by temple knights, swearing that day to banish darkness. Kind-hearted and courageous, she leads by example – shielding allies with her holy aura and smiting evil with uncompromising zeal.",
        "cards": [
            {"name": "Holy Strike", "type": "Damage", "cost": 1, "resource": "Stamina", "effect": "Strike an enemy with a melee weapon imbued with holy energy, dealing physical damage with added radiant harm especially effective against undead.", "star": "Damage scales with Strength; radiant bonus damage scales with Thaumaturgy"},
            {"name": "Searing Light", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Project a beam of searing holy light at a target, dealing radiant damage that ignores most armor.", "star": "Damage scales with Thaumaturgy; ignores a portion of target’s Resilience"},
            {"name": "Smite Evil", "type": "Damage", "cost": 4, "resource": "Mana", "effect": "Call down a pillar of holy light upon an enemy, dealing heavy holy damage.", "star": "Damage scales with Thaumaturgy; extra damage if target is undead or demon"},
            {"name": "Radiant Burst", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Release a burst of radiant energy around you, dealing moderate area damage to all nearby enemies.", "star": "Damage scales with Thaumaturgy; slight increase in radius with higher Resilience"},
            {"name": "Summon Guardian Angel", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a guardian angel ally. The angel periodically heals allies or strikes enemies with holy attacks.", "star": "Angel’s healing and damage scale with Thaumaturgy; duration extends with caster’s Resilience"},
            {"name": "Summon Spirit Weapon", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Conjure a floating holy weapon that fights independently for a short time.", "star": "Weapon’s attack damage scales with Strength; duration scales with Thaumaturgy"},
            {"name": "Divine Shield", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Create a divine shield barrier on the battlefield. It blocks enemy movement/attacks until it absorbs a certain amount of damage.", "star": "Shield’s HP scales with Resilience; size of shield slightly increases with Thaumaturgy"},
            {"name": "Beacon of Light", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Place a holy beacon structure on the ground. It emits light that slightly heals allies each turn and illuminates the area.", "star": "Healing aura power scales with Thaumaturgy; beacon radius increases with Resilience"},
            {"name": "Bless", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Bless an ally, improving their accuracy and damage slightly for a short duration.", "star": "No direct stat scaling; harder to dispel with higher Thaumaturgy"},
            {"name": "Shield of Faith", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Surround an ally with protective faith. Increases their Resilience, reducing damage taken.", "star": "Defense bonus scales with caster’s Resilience"},
            {"name": "Holy Weapon", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Imbue a target ally’s weapon with holy power. Their attacks deal extra radiant damage for a few turns.", "star": "Bonus damage scales with Thaumaturgy"},
            {"name": "Righteous Fury", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Fill an ally with fervor, greatly increasing their Strength and damage for their next attack or until end of turn.", "star": "Strength boost magnitude scales with Thaumaturgy"},
            {"name": "Banish", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Attempt to banish a summoned or undead creature from the battlefield.", "star": "Success chance increases with Thaumaturgy"},
            {"name": "Blinding Light", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Flash brilliant light at an enemy, blinding them temporarily.", "star": "Effect duration increases slightly with Thaumaturgy"},
            {"name": "Mark of Judgment", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Mark an enemy for divine retribution. They suffer lowered Resilience and take extra holy damage from attacks.", "star": "Resilience reduction scales with Thaumaturgy"},
            {"name": "Silence", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Fill the target’s mind with divine quiet. The enemy is unable to cast spells for a short time.", "star": "Duration extends with Thaumaturgy"},
            {"name": "Heal", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Call upon divine power to restore a significant amount of health to yourself or an ally.", "star": "Healing amount scales with Thaumaturgy and caster’s Resilience"},
            {"name": "Revive", "type": "Utility", "cost": 5, "resource": "Mana", "effect": "Channel holy energy to resurrect a fallen ally with a small portion of their health.", "star": "No stat influence on effect"},
            {"name": "Cleanse", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Purge an ally of negative effects (curses, poisons, debuffs).", "star": "No direct stat influence"},
            {"name": "Sanctuary", "type": "Utility", "cost": 3, "resource": "Mana", "effect": "Bless a target with sanctuary: for one turn, enemies cannot directly attack that target unless the target initiates combat.", "star": "No direct stat influence"}
        ]
    },
    "Malakai Dreadborne": {
        "class": "Warlock",
        "lore": "Malakai forged a pact with a shadow demon in exchange for power. Once a desperate scholar, he is now a warlock marked by dark runes on his arms. Though his soul is tainted, Malakai remains calculating and surprisingly honorable in his own way. He struggles between the demon’s whispers and his own will, walking the edge between damnation and the hope of breaking free.",
        "cards": [
            {"name": "Eldritch Blast", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Unleash a crackling bolt of otherworldly energy at a target, dealing forceful arcane damage.", "star": "Damage scales with Thaumaturgy"},
            {"name": "Hellfire", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Summon flames of the Nether to engulf an area, dealing fire damage in a small radius and igniting enemies.", "star": "Damage scales with Thaumaturgy; ignition burn intensity is Thaumaturgy-based"},
            {"name": "Soul Drain", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Tear at an enemy’s soul, dealing damage and converting a portion of it into health for the caster.", "star": "Damage and self-healing scale with Thaumaturgy; healing also influenced by Resilience"},
            {"name": "Curse of Agony", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Afflict an enemy with lingering agony, dealing damage over time.", "star": "Damage-over-time scales with Thaumaturgy"},
            {"name": "Summon Imp", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Summon a mischievous imp that casts small fireballs and can draw enemy attention with its taunts.", "star": "Imp’s damage scales with Thaumaturgy; distraction improves with caster’s Agility"},
            {"name": "Summon Felguard", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon a hulking felguard demon with strong melee attacks and high durability.", "star": "Felguard’s Strength and health scale with caster’s Strength and Resilience"},
            {"name": "Summon Shadowfiend", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Summon a shadowfiend – a fast-moving demon with high burst damage but low durability.", "star": "Shadowfiend’s damage scales with Thaumaturgy; its evasion scales with caster’s Agility"},
            {"name": "Summon Infernal", "type": "Summon", "cost": 6, "resource": "Mana", "effect": "Open a hellish portal and call forth an Infernal that slams the ground for area damage on arrival.", "star": "Infernal’s slam damage and strength scale with Thaumaturgy; durability scales with Resilience"},
            {"name": "Demonic Pact", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Channel demonic power, increasing your Thaumaturgy substantially for a short duration at the cost of some HP.", "star": "Thaumaturgy boost is fixed high; HP cost mitigated by high Resilience"},
            {"name": "Shadow Cloak", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Drape yourself in shadows, increasing your Agility for a few turns and granting temporary stealth if Thaumaturgy is high enough.", "star": "Agility boost scales with Thaumaturgy"},
            {"name": "Fel Armor", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Encase yourself in fel energy that increases Resilience and returns a small amount of damage to melee attackers.", "star": "Resilience buff scales with Resilience; damage return scales with Thaumaturgy"},
            {"name": "Unholy Frenzy", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Send your summoned demons into a frenzy. All your summons gain increased Strength and attack speed for a short time.", "star": "Summons’ Strength buff scales with caster’s Thaumaturgy"},
            {"name": "Curse of Weakness", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Curse an enemy, sapping their strength.", "star": "Strength reduction scales slightly with Thaumaturgy"},
            {"name": "Curse of Vulnerability", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Lay a curse making an enemy more susceptible to harm, lowering their Resilience.", "star": "Resilience reduction scales with Thaumaturgy"},
            {"name": "Doom", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Mark an enemy with impending doom that triggers massive damage after a short delay unless purged.", "star": "Damage when triggered scales with Thaumaturgy"},
            {"name": "Soul Shackle", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Bind an enemy’s soul with dark chains, rooting them in place and reducing Agility.", "star": "Duration extends slightly with higher Thaumaturgy"},
            {"name": "Life Tap", "type": "Utility", "cost": 0, "resource": "Mana", "effect": "Sacrifice a portion of your health to regain some mana.", "star": "Mana restored scales with Thaumaturgy; HP lost reduces with higher Resilience"},
            {"name": "Dark Portal", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Open a brief demonic portal and step through, teleporting to a visible location on the battlefield.", "star": "Teleport range slightly increases with Thaumaturgy"},
            {"name": "Sacrificial Pact", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Sacrifice one of your summoned demons to heal yourself substantially.", "star": "Healing gained scales with sacrificed creature’s max HP and caster’s Resilience"},
            {"name": "Nether Ward", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Conjure a ward that absorbs and negates the next incoming enemy spell or magical effect.", "star": "Ward duration scales with Thaumaturgy"}
        ]
    },
    "Zara Soulcaller": {
        "class": "Summoner",
        "lore": "A wanderer from distant deserts, Zara carries an ancient tome allowing her to call creatures from beyond. Curious and bold, she has befriended many of the beings she summons. Legends say a djinn blessed her family line – explaining her natural affinity for summoning. In battle, Zara is never alone; a menagerie of spirits and elemental allies fight at her side.",
        "cards": [
            {"name": "Arcane Bolt", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Fire a basic bolt of arcane energy at an enemy, dealing moderate magic damage.", "star": "Damage scales with Thaumaturgy"},
            {"name": "Ethereal Blades", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Conjure ethereal daggers and launch them at up to two targets.", "star": "Damage scales with Thaumaturgy; hitting multiple targets splits the damage"},
            {"name": "Spirit Blast", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Gather spiritual energy into a blast that strikes one target with high force.", "star": "Damage scales with Thaumaturgy; slight knockback if target’s Strength is low"},
            {"name": "Astral Wave", "type": "Damage", "cost": 4, "resource": "Mana", "effect": "Release a wave of astral energy that washes over all enemies, dealing area damage.", "star": "Damage scales with Thaumaturgy; less affected by enemy Resilience"},
            {"name": "Summon Stone Golem", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a sturdy stone golem with high HP and defense.", "star": "Golem’s health and Strength scale with caster’s Strength and Resilience"},
            {"name": "Summon Air Elemental", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon an air elemental that moves quickly and strikes with gusts.", "star": "Air elemental’s Agility and damage scale with Thaumaturgy; evasion scales with Agility"},
            {"name": "Summon Faerie Sprite", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Summon a tiny faerie sprite that provides minor healing or harasses enemies.", "star": "Sprite’s healing power scales with Thaumaturgy; distraction effectiveness scales with Agility"},
            {"name": "Summon Drake", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a small drake that breathes fire on enemies.", "star": "Drake’s fire damage scales with Thaumaturgy; flight effectiveness scales with Resilience"},
            {"name": "Empower Summon", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Channel energy into one of your summons, increasing that creature’s Strength and damage output.", "star": "Buff amount scales with caster’s Thaumaturgy"},
            {"name": "Shared Life", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Link your life force with a chosen summon, splitting damage taken between you and that summon for a duration.", "star": "Higher Resilience on both makes the link more effective"},
            {"name": "Planar Barrier", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Create a protective planar energy around yourself or an ally, increasing Resilience.", "star": "Defense bonus scales with Thaumaturgy"},
            {"name": "Circle of Summoning", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Conjure a magical circle that empowers all your summons.", "star": "Buff strength for summons scales with Thaumaturgy"},
            {"name": "Mark of the Summoner", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Mark an enemy, directing your minions’ wrath toward them.", "star": "Extra damage from summons scales with Thaumaturgy"},
            {"name": "Dismiss", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Forcibly dispel a summoned creature or construct.", "star": "Success chance and damage to target scales with Thaumaturgy"},
            {"name": "Enslave", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Dominate an enemy summoned creature, turning it to your side temporarily.", "star": "Success depends on caster’s Thaumaturgy versus target’s level"},
            {"name": "Binding Sigil", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Throw a runic sigil onto an enemy, restricting their movements.", "star": "Agility reduction scales with Thaumaturgy; duration scales slightly with Resilience"},
            {"name": "Planar Vision", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Peek briefly into other planes and the near future. Draw 2 cards or predict encounters.", "star": "No direct stat influence"},
            {"name": "Portal", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Open a small portal allowing you or an ally to instantly travel to another visible spot.", "star": "Range is fixed"},
            {"name": "Energy Well", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Tap into a latent energy well to restore some mana and stamina.", "star": "Resource restored scales with Thaumaturgy"},
            {"name": "Commanding Shout", "type": "Utility", "cost": 3, "resource": "Stamina", "effect": "Let out a rallying cry that urges all your summons to act immediately.", "star": "No direct stat scaling"}
        ]
    },
    "Lyra Mistbloom": {
        "class": "Illusionist",
        "lore": "Lyra is a whimsical yet mysterious enchantress from the Mistwood. Gifted with a silver tongue and sharp mind, she left the royal court to pursue true magical artistry – weaving illusions. Her playful demeanor masks a strategic thinker.",
        "cards": [
            {"name": "Mind Spike", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Shoot a spike of psychic energy into an enemy’s mind, dealing direct psychic damage that bypasses armor.", "star": "Damage scales with Thaumaturgy"},
            {"name": "Phantom Assault", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Conjure phantasmal duplicates to strike multiple foes.", "star": "Damage scales with Thaumaturgy; enemies with lower Resilience take full damage"},
            {"name": "Nightmare", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Force a target to witness a horrifying illusion, dealing psychic damage and briefly stunning them.", "star": "Damage and stun duration scale with Thaumaturgy"},
            {"name": "Dream Eater", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Induce a trance and siphon life from an enemy.", "star": "Damage and self-heal scale with Thaumaturgy; healing also influenced by Resilience"},
            {"name": "Illusory Double", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Create an illusory duplicate that draws enemy attacks and confuses foes.", "star": "Chance to be targeted scales with Thaumaturgy"},
            {"name": "Phantasmal Warrior", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon an illusion of a warrior to fight for you.", "star": "Damage treated as real to low Resilience enemies; scales with Thaumaturgy"},
            {"name": "Mirrored Wall", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Create an illusory wall or barrier.", "star": "Duration scales with Thaumaturgy"},
            {"name": "Phantasmal Dragon", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Conjure the illusion of a fearsome dragon, potentially frightening foes.", "star": "Apparent damage scales with Thaumaturgy"},
            {"name": "Invisibility", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Render yourself or an ally invisible for a short time or until they attack.", "star": "No direct stat scaling; duration may extend slightly with high Thaumaturgy"},
            {"name": "Mirror Image", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Create several illusory copies of yourself, increasing evasion.", "star": "Evasion chance increases with Thaumaturgy"},
            {"name": "Phantasmal Might", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Convince an ally of their great strength through illusion.", "star": "Strength increase is fixed; may last slightly longer with high Thaumaturgy"},
            {"name": "Phantom Shield", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Create illusory protective barriers, reducing damage taken.", "star": "Duration slightly enhanced by Thaumaturgy"},
            {"name": "Blindness", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Trick an enemy’s senses into darkness, greatly reducing their accuracy.", "star": "Duration extends with higher Thaumaturgy"},
            {"name": "Confusion", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Warp the target’s perception, potentially causing them to act randomly.", "star": "Harder to resist and lasts longer with high Thaumaturgy"},
            {"name": "Charm", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Beguile an enemy so they temporarily treat you as an ally.", "star": "Success chance increases with Thaumaturgy versus target’s Resilience"},
            {"name": "Sleep", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Magically lull an enemy into a deep sleep.", "star": "Higher Thaumaturgy increases number of targets or duration"},
            {"name": "Dispel Illusion", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "See through and dispel magical illusions in the area.", "star": "No direct stat influence"},
            {"name": "Read Mind", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Briefly read an enemy’s surface thoughts.", "star": "No direct stat scaling"},
            {"name": "Swap", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Instantly swap positions with a target creature via illusionary trickery.", "star": "No direct stat influence"},
            {"name": "Mass Hysteria", "type": "Utility", "cost": 4, "resource": "Mana", "effect": "Project chaotic illusions into all enemies’ minds, confusing them briefly.", "star": "Effect success on each enemy scales with Thaumaturgy versus their Resilience"}
        ]
    },
    "Gideon Spellfury": {
        "class": "Spellblade",
        "lore": "Gideon is a veteran knight who studied the arcane arts to become a spellblade. Formerly of the King’s Guard, he left after corrupt politics betrayed his unit. Hardened but just, Gideon now sells his skills as a monster-slayer. His blade dances with elemental fury, a testament to years of training to fuse steel and spell – making him a versatile force on any battlefield.",
        "cards": [
            {"name": "Arcane Strike", "type": "Damage", "cost": 1, "resource": "Stamina", "effect": "Strike an enemy with your weapon infused by arcane energy, dealing physical damage with a burst of magic.", "star": "Physical damage scales with Strength; arcane burst scales with Thaumaturgy"},
            {"name": "Whirling Blades", "type": "Damage", "cost": 2, "resource": "Stamina", "effect": "Spin with your weapon in a wide arc, hitting all adjacent enemies.", "star": "Damage scales with Strength; high Agility increases chance to hit all"},
            {"name": "Elemental Slash", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Imbue your blade with elemental power and slash an enemy, dealing extra elemental damage.", "star": "Base damage scales with Strength; elemental bonus scales with Thaumaturgy"},
            {"name": "Blade Wave", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Release a wave of energy from your blade hitting enemies in a line.", "star": "Damage scales with Thaumaturgy; range increases slightly with Strength"},
            {"name": "Spectral Sword", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Summon a floating spectral sword that fights alongside you for a short duration.", "star": "Sword’s damage scales with Strength; duration scales with Thaumaturgy"},
            {"name": "Arcane Shield", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Summon a magical shield that intercepts one incoming attack before shattering.", "star": "Can block stronger hits if caster’s Resilience is high"},
            {"name": "Familiar Spirit", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Summon a small arcane familiar that scouts and harasses enemies.", "star": "Familiar’s evasion and distraction ability scale with Agility"},
            {"name": "Binding Rune", "type": "Summon", "cost": 1, "resource": "Mana", "effect": "Place a magical rune trap on the ground that roots the first enemy to step on it.", "star": "Root duration fixed; higher Thaumaturgy increases trap duration if not triggered"},
            {"name": "Enchant Blade", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Temporarily enchant a weapon with arcane energy, increasing its damage and allowing it to strike ethereal creatures.", "star": "Damage bonus scales with Thaumaturgy; duration extends slightly with Resilience"},
            {"name": "Arcane Armor", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Coat yourself in a layer of arcane force, increasing Resilience and slightly reducing magic damage taken.", "star": "Resilience buff scales with Thaumaturgy; magic reduction increases with Resilience"},
            {"name": "Quickstep", "type": "Buff", "cost": 1, "resource": "Stamina", "effect": "Enter a battle trance that heightens reflexes, increasing your Agility for the rest of the turn.", "star": "Agility boost scales with Agility to a cap"},
            {"name": "Spell Reflection", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Create a reflective barrier for one turn. The next enemy spell that hits you is negated and reflected back.", "star": "Works once regardless of stats; high Thaumaturgy allows reflecting stronger spells"},
            {"name": "Hamstring", "type": "Debuff", "cost": 1, "resource": "Stamina", "effect": "Perform a precise strike at an enemy’s legs, reducing their Agility.", "star": "Agility reduction fixed; effect duration slightly longer with high Strength"},
            {"name": "Expose Weakness", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Magically analyze an enemy to reveal a weak point, lowering their Resilience.", "star": "Resilience reduction scales with Thaumaturgy"},
            {"name": "Mana Burn", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Charge your attack with disruptive energy and strike an enemy, burning away some of their mana.", "star": "Mana drained scales with Thaumaturgy; damage scales with Strength"},
            {"name": "Disarm", "type": "Debuff", "cost": 1, "resource": "Stamina", "effect": "Knock the weapon from an enemy’s hands, causing them to deal reduced damage until they recover.", "star": "Success chance higher if caster’s Strength exceeds target’s"},
            {"name": "Second Wind", "type": "Utility", "cost": 0, "resource": "Stamina", "effect": "Tap into endurance reserves to recover some health and stamina.", "star": "Restoration scales with Resilience"},
            {"name": "Meditation", "type": "Utility", "cost": 0, "resource": "Mana", "effect": "Briefly focus to regenerate some mana.", "star": "Mana restored scales with Thaumaturgy"},
            {"name": "Parry Stance", "type": "Utility", "cost": 1, "resource": "Stamina", "effect": "Adopt a defensive stance for the next attack; automatically parry it.", "star": "Works once; if Strength is higher you may riposte"},
            {"name": "Mana Shield", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Surround yourself with a shield that uses mana as protection.", "star": "Efficiency scales with Thaumaturgy"}
        ]
    },
    "Kairos Everwatch": {
        "class": "Chronomancer",
        "lore": "An eccentric time-mage, Kairos’s eyes glint with the sands of time. He spent decades studying temporal magic in an enchanted clock tower, giving him an ageless appearance. Often detached and cryptic, Kairos intervenes when time’s flow is threatened by reckless magic.",
        "cards": [
            {"name": "Time Bolt", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Fire a bolt of condensed time energy at an enemy.", "star": "Damage scales with Thaumaturgy"},
            {"name": "Temporal Slice", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Create a rift in time like a sharp blade and swing it through a target.", "star": "Damage scales with Thaumaturgy; bypasses defenses"},
            {"name": "Accelerate Decay", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Greatly accelerate an enemy’s aging and decay process.", "star": "Damage scales with Thaumaturgy; effective vs low Resilience"},
            {"name": "Chrono Burst", "type": "Damage", "cost": 4, "resource": "Mana", "effect": "Unleash a burst of temporal energy in an area.", "star": "Damage scales with Thaumaturgy; may also slow enemies"},
            {"name": "Time Spirit", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Summon a minor time elemental spirit that slows targets with its attacks.", "star": "Slowing potency scales with Thaumaturgy"},
            {"name": "Temporal Duplicate", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Pull a version of yourself from a few seconds in the future, mimicking your actions for a short time.", "star": "Effectiveness scales with Thaumaturgy"},
            {"name": "Hourglass Guardian", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon an hourglass golem that projects a slowing field.", "star": "Slowing aura strength scales with Thaumaturgy; durability scales with Resilience"},
            {"name": "Time Anchor", "type": "Summon", "cost": 2, "resource": "Mana", "effect": "Place a temporal anchor that slows enemies near it.", "star": "Anchor duration scales with Thaumaturgy"},
            {"name": "Haste", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Accelerate an ally’s personal time, greatly increasing their Agility for a short duration.", "star": "Agility boost scales with Thaumaturgy"},
            {"name": "Time Shield", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Wrap an ally in a time-distortion field that halves damage from the next hit.", "star": "Duration scales with Thaumaturgy if hit is not taken immediately"},
            {"name": "Rewind", "type": "Buff", "cost": 3, "resource": "Mana", "effect": "Revert a target to their state a few moments ago, restoring some HP.", "star": "Healing amount scales with Thaumaturgy"},
            {"name": "Time Warp", "type": "Buff", "cost": 4, "resource": "Mana", "effect": "Briefly warp time for all allies, allowing an extra minor action this turn.", "star": "No direct stat scaling"},
            {"name": "Slow", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Drastically slow an enemy’s personal time, reducing their Agility.", "star": "Agility reduction scales with Thaumaturgy"},
            {"name": "Temporal Lock", "type": "Debuff", "cost": 3, "resource": "Mana", "effect": "Freeze an enemy in time for a moment.", "star": "Duration scales slightly with Thaumaturgy"},
            {"name": "Wither (Age)", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Age a target unnaturally, lowering Strength and Resilience.", "star": "Stat reductions scale with Thaumaturgy"},
            {"name": "Stasis Field", "type": "Debuff", "cost": 4, "resource": "Mana", "effect": "Project a bubble of slowed time in an area.", "star": "Field size and intensity scale with Thaumaturgy"},
            {"name": "Time Jump", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Teleport yourself a short distance by stepping out of time.", "star": "Range fixed"},
            {"name": "Chrono Vision", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Glance a few seconds into the future to anticipate enemy actions or draw a card.", "star": "No direct stat influence"},
            {"name": "Time Stop", "type": "Utility", "cost": 5, "resource": "Mana", "effect": "Stop time for everyone except yourself for a brief moment.", "star": "Requires very high Thaumaturgy"},
            {"name": "Phase Out", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Shift a target slightly out of the timeline, making them untargetable for one turn.", "star": "Duration is one turn"}
        ]
    },
    "Zephyra Stormsoul": {
        "class": "Storm Mage",
        "lore": "Born amidst a raging storm at sea, Zephyra studied under an air djinn to hone her tempest powers. Free-spirited and proud, her emotions often mirror the weather. She defends the downtrodden, striking down tyrants with bolts of lightning and howling winds.",
        "cards": [
            {"name": "Lightning Bolt", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Strike a single enemy with a powerful bolt of lightning.", "star": "Damage scales with Thaumaturgy; stun chance increases with Thaumaturgy"},
            {"name": "Chain Lightning", "type": "Damage", "cost": 3, "resource": "Mana", "effect": "Unleash a lightning bolt that jumps to additional enemies.", "star": "Damage scales with Thaumaturgy; each jump loses power unless Thaumaturgy is high"},
            {"name": "Thunderclap", "type": "Damage", "cost": 2, "resource": "Mana", "effect": "Create a deafening thunderclap around yourself, damaging nearby enemies and potentially stunning them.", "star": "Damage scales with Thaumaturgy; stun chance scales with Strength"},
            {"name": "Wind Slash", "type": "Damage", "cost": 1, "resource": "Mana", "effect": "Hurl a blade of razor-sharp wind at an enemy.", "star": "Damage scales with Thaumaturgy; knockback chance if Agility high"},
            {"name": "Summon Storm Elemental", "type": "Summon", "cost": 5, "resource": "Mana", "effect": "Summon a storm elemental that zaps enemies with lightning.", "star": "Elemental’s damage scales with Thaumaturgy; resilience scales with Resilience"},
            {"name": "Summon Thunderbird", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Summon a mighty thunderbird that strikes foes with lightning from above.", "star": "Thunderbird’s damage scales with Thaumaturgy; evasion scales with Agility"},
            {"name": "Tempest Totem", "type": "Summon", "cost": 3, "resource": "Mana", "effect": "Erect a totem crackling with lightning that strikes random nearby enemies each turn.", "star": "Damage per strike scales with Thaumaturgy; duration scales with Resilience"},
            {"name": "Tornado", "type": "Summon", "cost": 4, "resource": "Mana", "effect": "Conjure a roaming tornado that persists for a short time.", "star": "Damage and duration scale with Thaumaturgy"},
            {"name": "Storm Shield", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Wrap an ally in swirling winds and static, increasing Resilience and shocking melee attackers.", "star": "Resilience boost scales with Resilience; shock damage scales with Thaumaturgy"},
            {"name": "Static Charge", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Build up static electricity on a target, giving their next attack bonus lightning damage and shocking their attacker when hit.", "star": "Bonus damage scales with Thaumaturgy"},
            {"name": "Windstep", "type": "Buff", "cost": 1, "resource": "Mana", "effect": "Imbue an ally with wind’s speed, increasing Agility for one turn.", "star": "Agility boost scales with Thaumaturgy"},
            {"name": "Charged Weapon", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Enchant an ally’s weapon with thunder, adding lightning damage and a chance to stun.", "star": "Bonus damage and stun chance scale with Thaumaturgy"},
            {"name": "Shock", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Zap a target with electricity, dealing small damage and stunning briefly.", "star": "Damage scales slightly with Thaumaturgy"},
            {"name": "Deafening Thunder", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Deafen and disorient a target, lowering accuracy and potentially interrupting casting.", "star": "Interruption chance higher if caster’s Thaumaturgy exceeds target’s"},
            {"name": "Static Field", "type": "Debuff", "cost": 2, "resource": "Mana", "effect": "Surround an enemy with static, dealing damage over time and reducing Agility.", "star": "Damage and Agility reduction scale with Thaumaturgy"},
            {"name": "Soak", "type": "Debuff", "cost": 1, "resource": "Mana", "effect": "Drench an enemy in conjured rain, slowing them and making them vulnerable to electricity.", "star": "Lightning vulnerability scales with Thaumaturgy"},
            {"name": "Flight", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Grant yourself or an ally the power of flight for a few turns.", "star": "Duration fixed"},
            {"name": "Gust", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Create a strong gust to push an enemy or object.", "star": "Push distance fixed; heavier enemies may resist"},
            {"name": "Recharge", "type": "Utility", "cost": 1, "resource": "Mana", "effect": "Draw static energy from the environment to restore a small amount of mana.", "star": "Mana restored scales with Thaumaturgy"},
            {"name": "Cloud Form", "type": "Utility", "cost": 2, "resource": "Mana", "effect": "Turn yourself into a misty cloud briefly, becoming immune to physical damage for one turn.", "star": "No direct stat scaling"}
        ]
    },
    "Selene Nightwhisper": {
        "class": "Assassin",
        "lore": "Selene is a shadowy assassin from the Nightblade guild. Raised in the slums and trained by a secret order, she eliminates targets with stealth and precision, striking from the darkness.",
        "cards": [
            {"name": "Backstab", "type": "Damage", "cost": 2, "resource": "Stamina", "effect": "Deliver a devastating strike to an unaware enemy.", "star": "Damage scales with Strength; crit chance with Agility"},
            {"name": "Poisoned Dagger", "type": "Damage", "cost": 1, "resource": "Stamina", "effect": "Stab with a poisoned blade, applying damage over time.", "star": "Poison scales with Thaumaturgy"},
            {"name": "Shadowmeld", "type": "Buff", "cost": 2, "resource": "Mana", "effect": "Become invisible for a short duration, empowering your next attack.", "star": "Bonus damage scales with Thaumaturgy"},
            {"name": "Poison Trap", "type": "Summon", "cost": 2, "resource": "Stamina", "effect": "Lay a hidden trap that poisons the first enemy to step on it.", "star": "Potency scales with Thaumaturgy"},
            {"name": "Vanish", "type": "Utility", "cost": 1, "resource": "Stamina", "effect": "Disappear in a puff of smoke, avoiding attacks for the rest of the turn.", "star": "Range increases with Agility"}
        ]
    },
    "Ragnar Ironhide": {
        "class": "Berserker",
        "lore": "Ragnar hails from the frozen north. Nicknamed 'Ironhide' for his toughness, he wields his greataxe with unstoppable fury, shrugging off wounds as he goes berserk in battle.",
        "cards": [
            {"name": "Heavy Strike", "type": "Damage", "cost": 1, "resource": "Stamina", "effect": "Deliver a heavy overhead swing with your weapon.", "star": "Damage scales with Strength"},
            {"name": "Cleave", "type": "Damage", "cost": 2, "resource": "Stamina", "effect": "Swing in a wide arc hitting two enemies.", "star": "Damage scales with Strength"},
            {"name": "Berserker Rage", "type": "Buff", "cost": 2, "resource": "Stamina", "effect": "Enter a furious rage increasing Strength and Resilience for a few turns.", "star": "Bonuses fixed; damage taken reduced if Resilience high"},
            {"name": "War Banner", "type": "Summon", "cost": 3, "resource": "Stamina", "effect": "Plant a banner inspiring allies and intimidating foes.", "star": "Buff scales with Strength"},
            {"name": "Second Wind", "type": "Utility", "cost": 0, "resource": "Stamina", "effect": "Recover some health and stamina once per fight.", "star": "Restoration scales with Resilience"}
        ]
    }
}

UNIVERSAL_CARDS = [
    {"name": "Strike", "cost": 1, "resource": "Stamina", "effect": "Perform a basic attack with your equipped weapon.", "purpose": "Offense – damage based on Strength", "category": "universal", "rarity": "common"},
    {"name": "Defend", "cost": 1, "resource": "Stamina", "effect": "Adopt a defensive stance, reducing damage taken until your next turn.", "purpose": "Defense – increases Resilience briefly", "category": "universal", "rarity": "common"},
    {"name": "Dodge", "cost": 1, "resource": "Stamina", "effect": "Make an evasive maneuver to avoid the next attack.", "purpose": "Defense – negates one incoming attack", "category": "universal", "rarity": "common"},
    {"name": "Healing Potion", "cost": 0, "resource": "Stamina", "effect": "Consume a potion to restore a moderate amount of health.", "purpose": "Recovery – heals HP", "category": "universal", "rarity": "common"},
    {"name": "Mana Potion", "cost": 0, "resource": "Stamina", "effect": "Drink a potion to regain a moderate amount of mana.", "purpose": "Recovery – restores mana", "category": "universal", "rarity": "common"},
    {"name": "Focus", "cost": 1, "resource": "Mana", "effect": "Concentrate to sharpen your next action.", "purpose": "Buff – improves offense of your next move", "category": "universal", "rarity": "uncommon"},
    {"name": "Dispel Magic", "cost": 2, "resource": "Mana", "effect": "Release a burst of nullifying energy to remove a magical buff from an enemy or a debuff from an ally.", "purpose": "Utility – negates one magical effect", "category": "universal", "rarity": "uncommon"},
    {"name": "Retreat", "cost": 1, "resource": "Stamina", "effect": "Swiftly move out of melee range without provoking attacks.", "purpose": "Mobility – reposition safely", "category": "universal", "rarity": "common"},
    {"name": "Inspire", "cost": 1, "resource": "Stamina", "effect": "Encourage an ally, boosting their damage and resistance for a short time.", "purpose": "Support – minor all-round boost", "category": "universal", "rarity": "uncommon"},
    {"name": "Plan Ahead", "cost": 1, "resource": "Mana", "effect": "Take time to strategize, drawing 2 extra cards from your deck.", "purpose": "Utility – increases your options", "category": "universal", "rarity": "rare"}
]

# Per-character stat progressions used by the leveling system
CHARACTER_PROGRESSIONS = {
    "Aurelia Flameheart": {
        "base_hp": 16, "hp_per_level": 4,
        "base_mana": 25, "mana_per_level": 5,
        "base_stamina": 8, "stamina_per_level": 2,
        "hp_regen": 0.5, "hp_regen_per_level": 0.1,
        "mana_regen": 1.2, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.3, "stamina_regen_per_level": 0.1
    },
    "Darius Nightshade": {
        "base_hp": 15, "hp_per_level": 3,
        "base_mana": 25, "mana_per_level": 5,
        "base_stamina": 8, "stamina_per_level": 1,
        "hp_regen": 0.4, "hp_regen_per_level": 0.1,
        "mana_regen": 1.2, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.3, "stamina_regen_per_level": 0.05
    },
    "Thorne Oakenshade": {
        "base_hp": 20, "hp_per_level": 5,
        "base_mana": 20, "mana_per_level": 4,
        "base_stamina": 12, "stamina_per_level": 3,
        "hp_regen": 0.8, "hp_regen_per_level": 0.1,
        "mana_regen": 0.8, "mana_regen_per_level": 0.1,
        "stamina_regen": 0.8, "stamina_regen_per_level": 0.1
    },
    "Seraphina Dawnshield": {
        "base_hp": 22, "hp_per_level": 5,
        "base_mana": 20, "mana_per_level": 4,
        "base_stamina": 12, "stamina_per_level": 3,
        "hp_regen": 1.0, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.5, "stamina_regen_per_level": 0.1
    },
    "Malakai Dreadborne": {
        "base_hp": 18, "hp_per_level": 3,
        "base_mana": 22, "mana_per_level": 5,
        "base_stamina": 8, "stamina_per_level": 2,
        "hp_regen": 0.5, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.4, "stamina_regen_per_level": 0.1
    },
    "Zara Soulcaller": {
        "base_hp": 18, "hp_per_level": 4,
        "base_mana": 22, "mana_per_level": 5,
        "base_stamina": 10, "stamina_per_level": 2,
        "hp_regen": 0.5, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.5, "stamina_regen_per_level": 0.1
    },
    "Lyra Mistbloom": {
        "base_hp": 14, "hp_per_level": 3,
        "base_mana": 24, "mana_per_level": 5,
        "base_stamina": 8, "stamina_per_level": 2,
        "hp_regen": 0.4, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.3, "stamina_regen_per_level": 0.1
    },
    "Gideon Spellfury": {
        "base_hp": 25, "hp_per_level": 6,
        "base_mana": 15, "mana_per_level": 3,
        "base_stamina": 15, "stamina_per_level": 3,
        "hp_regen": 1.0, "hp_regen_per_level": 0.1,
        "mana_regen": 0.8, "mana_regen_per_level": 0.1,
        "stamina_regen": 1.0, "stamina_regen_per_level": 0.1
    },
    "Kairos Everwatch": {
        "base_hp": 15, "hp_per_level": 3,
        "base_mana": 24, "mana_per_level": 5,
        "base_stamina": 8, "stamina_per_level": 2,
        "hp_regen": 0.4, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.3, "stamina_regen_per_level": 0.1
    },
    "Zephyra Stormsoul": {
        "base_hp": 18, "hp_per_level": 4,
        "base_mana": 22, "mana_per_level": 4,
        "base_stamina": 10, "stamina_per_level": 2,
        "hp_regen": 0.6, "hp_regen_per_level": 0.1,
        "mana_regen": 1.0, "mana_regen_per_level": 0.2,
        "stamina_regen": 0.4, "stamina_regen_per_level": 0.1
    },
    "Selene Nightwhisper": {
        "base_hp": 20, "hp_per_level": 4,
        "base_mana": 10, "mana_per_level": 2,
        "base_stamina": 18, "stamina_per_level": 4,
        "hp_regen": 0.5, "hp_regen_per_level": 0.1,
        "mana_regen": 0.5, "mana_regen_per_level": 0.1,
        "stamina_regen": 1.2, "stamina_regen_per_level": 0.2
    },
    "Ragnar Ironhide": {
        "base_hp": 30, "hp_per_level": 8,
        "base_mana": 5, "mana_per_level": 1,
        "base_stamina": 20, "stamina_per_level": 5,
        "hp_regen": 1.5, "hp_regen_per_level": 0.2,
        "mana_regen": 0.2, "mana_regen_per_level": 0.1,
        "stamina_regen": 2.0, "stamina_regen_per_level": 0.2
    }
}
