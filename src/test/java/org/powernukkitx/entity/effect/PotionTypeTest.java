package org.powernukkitx.entity.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PotionTypeTest {

    @Test
    void fullConstructorExposesFields() {
        PotionType type = new PotionType("Foo", "minecraft:foo", 7, 3, PotionEffectDefinition.EMPTY);
        assertEquals("Foo", type.name());
        assertEquals("minecraft:foo", type.stringId());
        assertEquals(7, type.id());
        assertEquals(3, type.level());
        assertSame(PotionEffectDefinition.EMPTY, type.effects());
    }

    @Test
    void shortConstructorDefaultsLevelToOne() {
        PotionType type = new PotionType("Foo", "minecraft:foo", 7, PotionEffectDefinition.EMPTY);
        assertEquals(1, type.level());
    }

    @Test
    void knownConstantsHaveExpectedValues() {
        assertEquals(0, PotionType.WATER.id());
        assertEquals("minecraft:water", PotionType.WATER.stringId());
        assertEquals(2, PotionType.LEAPING_STRONG.level());
    }

    @Test
    void equalsMatchesOnStringIdAndId() {
        assertEquals(new PotionType("X", "minecraft:water", 0, PotionEffectDefinition.EMPTY), PotionType.WATER);
        assertNotEquals(PotionType.WATER, PotionType.MUNDANE);
    }

    @Test
    void romanLevelForLevelOne() {
        assertEquals("I", PotionType.WATER.getRomanLevel());
    }

    @Test
    void romanLevelForLevelTwo() {
        assertEquals("II", PotionType.LEAPING_STRONG.getRomanLevel());
    }

    @Test
    void romanLevelZero() {
        PotionType zero = new PotionType("Z", "minecraft:z", 100, 0, PotionEffectDefinition.EMPTY);
        assertEquals("0", zero.getRomanLevel());
    }

    @Test
    void romanLevelNegativePrefixesMinus() {
        PotionType neg = new PotionType("N", "minecraft:n", 101, -4, PotionEffectDefinition.EMPTY);
        assertEquals("-IV", neg.getRomanLevel());
    }
}
