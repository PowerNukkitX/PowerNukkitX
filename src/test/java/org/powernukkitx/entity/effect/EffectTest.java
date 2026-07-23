package org.powernukkitx.entity.effect;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class EffectTest {

    @Test
    void defaultsFromConstructor() {
        Effect effect = new EffectSpeed();
        assertSame(EffectType.SPEED, effect.getType());
        assertEquals(EffectType.SPEED.id(), effect.getId());
        assertEquals("%potion.moveSpeed", effect.getName());
        assertEquals(600, effect.getDuration());
        assertEquals(0, effect.getAmplifier());
        assertEquals(1, effect.getLevel());
        assertTrue(effect.isVisible());
        assertFalse(effect.isAmbient());
        assertFalse(effect.isBad());
    }

    @Test
    void badFlagFromConstructor() {
        assertTrue(new EffectSlowness().isBad());
    }

    @Test
    void settersAreFluentAndMutate() {
        Effect effect = new EffectSpeed();
        assertSame(effect, effect.setDuration(100));
        assertEquals(100, effect.getDuration());

        effect.setAmplifier(2);
        assertEquals(2, effect.getAmplifier());
        assertEquals(3, effect.getLevel());

        effect.setVisible(false);
        assertFalse(effect.isVisible());

        effect.setAmbient(true);
        assertTrue(effect.isAmbient());

        Color color = new Color(1, 2, 3);
        effect.setColor(color);
        assertEquals(color, effect.getColor());
    }

    @Test
    void infiniteDuration() {
        Effect effect = new EffectSpeed();
        assertFalse(effect.isInfinite());
        assertSame(effect, effect.setInfinite());
        assertEquals(-1, effect.getDuration());
        assertTrue(effect.isInfinite());
    }

    @Test
    void baseEffectHasNoInterval() {
        assertEquals(0, new EffectSpeed().getInterval());
        assertFalse(new EffectSpeed().canTick());
    }

    @Test
    void canTickOnlyOnIntervalMultiples() {
        Effect poison = new EffectPoison();
        int interval = poison.getInterval();
        assertTrue(interval > 0);
        assertTrue(poison.canTick(interval * 3));
        assertFalse(poison.canTick(interval * 3 + 1));
    }

    @Test
    void instantEffectDurationAndInterval() {
        Effect health = new EffectInstantHealth();
        assertEquals(1, health.getDuration());
        assertEquals(1, health.getInterval());
    }

    @Test
    void cloneIsIndependentCopy() {
        Effect original = new EffectSpeed().setDuration(42).setAmplifier(4);
        Effect copy = original.clone();
        assertNotSame(original, copy);
        assertEquals(42, copy.getDuration());
        assertEquals(4, copy.getAmplifier());
        assertSame(original.getType(), copy.getType());

        copy.setDuration(7);
        assertEquals(42, original.getDuration());
    }
}
