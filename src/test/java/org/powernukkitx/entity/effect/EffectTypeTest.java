package org.powernukkitx.entity.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectTypeTest {

    @Test
    void recordAccessorsReturnConstructorArgs() {
        EffectType type = new EffectType("custom", 99);
        assertEquals("custom", type.stringId());
        assertEquals(99, type.id());
    }

    @Test
    void knownConstantsHaveExpectedIds() {
        assertEquals(1, EffectType.SPEED.id());
        assertEquals("speed", EffectType.SPEED.stringId());
        assertEquals(31, EffectType.TRIAL_OMEN.id());
        assertEquals("trial_omen", EffectType.TRIAL_OMEN.stringId());
    }

    @Test
    void equalsMatchesOnStringIdAndId() {
        assertEquals(new EffectType("speed", 1), EffectType.SPEED);
        assertNotEquals(EffectType.SPEED, EffectType.SLOWNESS);
    }

    @Test
    void equalsRejectsForeignTypes() {
        assertNotEquals("speed", EffectType.SPEED);
        assertNotEquals(null, EffectType.SPEED);
    }
}
