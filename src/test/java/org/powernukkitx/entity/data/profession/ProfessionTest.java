package org.powernukkitx.entity.data.profession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.level.Sound;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ProfessionTest {

    @BeforeAll
    static void setup() {
        Profession.init();
    }

    @Test
    void getProfessionsReturnsDefensiveCopy() {
        HashMap<Integer, Profession> a = Profession.getProfessions();
        HashMap<Integer, Profession> b = Profession.getProfessions();
        assertNotSame(a, b);
        assertFalse(a.isEmpty());
    }

    @Test
    void lookupByIndexReturnsMatchingProfession() {
        Profession armor = Profession.getProfession(8);
        assertNotNull(armor);
        assertInstanceOf(ProfessionArmor.class, armor);
        assertEquals(8, armor.getIndex());
    }

    @Test
    void unknownIndexReturnsNull() {
        assertNull(Profession.getProfession(-999));
    }

    @Test
    void gettersReturnConstructorValues() {
        Profession armor = new ProfessionArmor();
        assertEquals(8, armor.getIndex());
        assertEquals("entity.villager.armor", armor.getName());
        assertNotNull(armor.getBlockID());
        assertSame(Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, armor.getWorkSound());
    }

    @Test
    void indexKeysMatchProfessionIndex() {
        Profession.getProfessions().forEach((index, profession) ->
                assertEquals(index.intValue(), profession.getIndex()));
    }
}
