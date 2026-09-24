package com.infiniteconquest.cli;
import com.infiniteconquest.core.*;
import com.infiniteconquest.data.Keyword;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class TerrainCatalogTest {
    @Test void allTwelveKeywordsExistOnPlayableCardsWithSixOnEachDevelopmentType() {
        var pool=new PrototypeCardPool();
        assertEquals(6,TerrainRules.landKeywords().size());assertEquals(6,TerrainRules.structureKeywords().size());
        for(var keyword:TerrainRules.landKeywords())assertTrue(pool.cards().stream().anyMatch(c->c.type()==CardType.LAND && c.hasKeyword(keyword)),keyword.name());
        for(var keyword:TerrainRules.structureKeywords())assertTrue(pool.cards().stream().anyMatch(c->c.type()==CardType.STRUCTURE && c.hasKeyword(keyword)),keyword.name());
        assertEquals(14,pool.cards().stream().filter(c->c.developmentGoldCost()>0).count());
        assertEquals(0,pool.require("zeus_olympian_cloudbank").goldCost());
        assertEquals(2,pool.require("ares_ballistic_shrine").developmentGoldCost());
        assertEquals(2,pool.require("ares_ballistic_shrine").cost());
        assertEquals(2,pool.require("ares_ballistic_shrine").keywordValue(Keyword.TURRET).range());
    }
    @Test void archetypesAreExplicitAndSearchableWithoutBecomingCombatKeywords() {
        var pool=new PrototypeCardPool();
        assertTrue(pool.require("ares_redline_recruit").archetypes().contains("HUMAN"));
        assertTrue(pool.require("hephaestus_bronze_assembly_bot").archetypes().contains("AUTOMATON"));
        assertTrue(pool.require("poseidon_triton_waveguard").archetypes().contains("MERFOLK"));
        assertTrue(pool.require("zeus_chain_lightning").archetypes().contains("STORM"));
        assertTrue(pool.require("zeus_cloudwall_bastion").archetypes().contains("FORTRESS"));
        assertTrue(pool.require("zeus_eagles_perch_array").archetypes().contains("HIGHLAND"));
        assertTrue(pool.cards().stream().anyMatch(c->c.archetypes().isEmpty()));
    }
    @Test void botsAndHintsRespectTheGoldSurcharge() {
        var factory=new DemoMatchFactory();var s=factory.create(42L);
        while(s.phase()==Phase.PLAY && s.personalTurnNumber(s.activePlayer())<3)new GameEngine().apply(s,new GameAction.EndTurn(s.activePlayer()));
        int owner=s.activePlayer();var card=new CardInstance(UUID.randomUUID(),factory.pool().require("zeus_eagles_perch_array"),owner,Zone.HAND);s.register(card);s.player(owner).addToHand(card.instanceId());
        s.player(owner).spendGp(s.player(owner).currentGp());int index=s.player(owner).hand().indexOf(card.instanceId());
        assertTrue(new ActionHints().forActivePlayer(s,new GameEngine()).stream().noneMatch(c->c.startsWith("play "+index+" ")));
        s.player(owner).restoreGp(1);
        assertTrue(new ActionHints().forActivePlayer(s,new GameEngine()).stream().anyMatch(c->c.startsWith("play "+index+" ")));
    }
}
