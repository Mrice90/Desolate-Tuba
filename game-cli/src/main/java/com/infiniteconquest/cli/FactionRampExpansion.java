package com.infiniteconquest.cli;

import com.infiniteconquest.core.*;

import java.util.*;

/** Forty development cards per faction: two Lands and two Structures at each turn value 1-10. */
final class FactionRampExpansion {
    private static final Map<String, List<String>> LAND_NAMES = Map.of(
            "ZEUS", List.of("Cloudstep Meadow","Static-Crowned Bluff","Thunderhead Shelf","Aerie of First Light","Stormglass Vale","Nimbus Causeway","Eaglewind Heights","Rain-Scribed Plateau","Boltcarved Summit","Tempest Verge","Oracle's Horizon","Skyfather's Reach","Ionized Cloudbank","Celestial Updraft","Olympian Ridgeline","Fulminant Expanse","Heaven-Split Mesa","Crown of Storms","Worldbolt Pinnacle","Empyrean Apex"),
            "POSEIDON", List.of("Tidepool Reach","Pearlwater Shoal","Foamwake Strand","Coralroad Shelf","Siren's Lagoon","Moon-Tide Estuary","Trident Current","Deepglass Reef","Nereid Crossing","Leviathan Trench","Sunken Orchard","Abyssal Garden","Maelstrom Basin","Whale-Road Expanse","Temple Current","Crown Reef","Worldsea Shelf","Oceanheart Chasm","Endless Undertow","Thalassic Throne"),
            "HADES", List.of("Ashen Footpath","Obol-Strewn Bank","Whispering Barrow","Cinderroot Hollow","Lethe Ford","Mourning Field","Styxward Crossing","Pomegranate Vale","Shadebound Moor","Sepulcher Plain","Eidolon Basin","Gravefire Fen","Elysian Verge","Tartarean Shelf","Black Cypress Reach","Soulwind Waste","Kingdom Below","Final Passage","Deathless Expanse","Underworld Crown"),
            "ARES", List.of("Dust of Challenge","Blood-Red Scrub","Spearpoint Field","Warpath Ford","Bronzegrass Steppe","Drumbeat Ridge","Contest Ground","Shieldbreak Plain","Phalanx Road","Lionblood Mesa","Conqueror's March","Scorched Muster","Red Standard Vale","Titan's Footfall","Siegeway Expanse","Victory Scar","Unbroken Front","Godwar Plateau","World-Sunder Field","Ares Ascendant"),
            "ATHENA", List.of("Olivetree Court","Measured Terrace","Owlwatch Garden","Marble Lesson Yard","Strategist's Walk","Silvered Agora","Aegis Grove","Geometer's Rise","Quiet Debate Field","Pallas Promenade","Academy Precinct","Labyrinth Overlook","Victory-Olive Heights","Constellation Court","Parthenon Approach","Wisdom's Province","Grand Symposium","Polis of Foresight","Perfect Formation","Athena's Design"),
            "HEPHAESTUS", List.of("Coalbright Seam","Coppervein Yard","Anvilstone Flats","Sootwind Quarry","Ironroot Shelf","Bellows Ravine","Bronze Riverbed","Gearsoil Terrace","Magma Conduit","Adamant Lode","Cyclopean Excavation","Living Ore Basin","Furnacefault Expanse","Titanmetal Reach","Worldforge Caldera","Unquenched Province","Divine Alloy Field","Mantle Crucible","Planetary Foundry","Core of Creation")
    );
    private static final Map<String, List<String>> STRUCTURE_NAMES = Map.of(
            "ZEUS", List.of("Kite-Signal Post","Cloudglass Pylon","Thunder Relay","Aerie Watchtower","Stormcall Shrine","Nimbus Observatory","Eagle Beacon","Rain Oracle Spire","Bolt Conductor","Tempest Bastion","Horizon Augury","Sky-Lance Battery","Ion Crown Array","Celestial Gatehouse","Olympian Weatherworks","Fulminant Citadel","Heaven Bridge Nexus","Crownstorm Palace","Worldbolt Engine","Empyrean Throne"),
            "POSEIDON", List.of("Tide Gauge","Pearl Diver Lodge","Foamwall Jetty","Coralway Tower","Siren Bell","Moon-Tide Reservoir","Trident Lighthouse","Deepglass Bulwark","Nereid Sanctuary","Leviathan Dock","Sunken Conservatory","Abyssal Aqueduct","Maelstrom Engine","Whale-Road Harbor","Temple of Currents","Crown Reef Keep","Worldsea Gate","Oceanheart Palace","Endless Tideworks","Thalassic Citadel"),
            "HADES", List.of("Obol Tollhouse","Ash Lantern","Barrow Gate","Cinderroot Ossuary","Lethe Well","Mourner's Hall","Styx Chain Tower","Pomegranate Shrine","Shade Barracks","Sepulcher Keep","Eidolon Archive","Gravefire Furnace","Elysian Portal","Tartarean Lock","Black Cypress Court","Soulwind Necropolis","Kingdom-Below Gate","Final Passage House","Deathless Vault","Underworld Palace"),
            "ARES", List.of("Challenge Drum","Blood Banner Post","Spear Rack Tower","Warpath Camp","Bronzegrass Redoubt","Drumbeat Fortress","Arena Gate","Shieldbreak Ramworks","Phalanx Barracks","Lionblood Keep","Conqueror's Arch","Scorched Arsenal","Red Standard Citadel","Titan Mustering Hall","Siegeway Foundry","Victory Monument","Unbroken Warcamp","Godwar Engine","World-Sunder Bastion","Ares War Palace"),
            "ATHENA", List.of("Olive Scriptorium","Measured Watch","Owl Archive","Marble Classroom","Strategist's Table","Silver Agora Hall","Aegis Academy","Geometer's Tower","Debate Chamber","Pallas Colonnade","Grand Academy","Labyrinth Bureau","Victory-Olive Temple","Constellation Observatory","Parthenon Annex","Wisdom Council","Grand Symposium Hall","Polis Commandery","Perfect Formation Keep","Athena's High Archive"),
            "HEPHAESTUS", List.of("Coalbright Kiln","Copper Press","Anvilstone Shop","Sootwind Smelter","Ironroot Forge","Bellows Tower","Bronze River Mill","Gearsoil Workshop","Magma Pump","Adamant Foundry","Cyclopean Crane","Living Ore Refinery","Furnacefault Engine","Titanmetal Assembly","Worldforge Works","Unquenched Factory","Divine Alloy Crucible","Mantle Machinehall","Planetary Foundry Core","Creation Engine")
    );

    private FactionRampExpansion() { }

    static List<CardDefinition> cards() {
        List<CardDefinition> result = new ArrayList<>();
        for (String faction : FactionDecks.FACTIONS.stream().sorted().toList()) {
            List<String> lands = LAND_NAMES.get(faction);
            List<String> structures = STRUCTURE_NAMES.get(faction);
            for (int tier = 1; tier <= 10; tier++) {
                for (int variant = 0; variant < 2; variant++) {
                    int index = (tier - 1) * 2 + variant;
                    result.add(development(faction, lands.get(index), CardType.LAND, tier, variant));
                    result.add(development(faction, structures.get(index), CardType.STRUCTURE, tier, variant));
                }
            }
        }
        return List.copyOf(result);
    }

    private static CardDefinition development(String faction, String name, CardType type, int tier, int variant) {
        int gp = 1 + (tier - 1) / 3;
        int hp = (type == CardType.LAND ? 6 : 8) + tier * 2 + variant;
        int activationCost = 1 + (tier - 1) / 4;
        AbilityEffectType draw = type == CardType.LAND
                ? AbilityEffectType.DRAW_STRUCTURE : AbilityEffectType.DRAW_CHARACTER;
        String id = faction.toLowerCase(Locale.ROOT) + "_ramp_" + type.name().toLowerCase(Locale.ROOT)
                + "_" + tier + "_" + (variant == 0 ? "a" : "b");
        return new CardDefinition(id, name, type, faction, tier, 0, 0, 0, 0, hp,
                Set.of(), List.of(), gp, DevelopmentPassive.NONE,
                List.of(new CardAbility(AbilityTrigger.ACTIVATED, draw, 1, activationCost)));
    }
}
