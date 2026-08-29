package org.powernukkitx.entity.effect;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PotionEffectDefinitionTest {

    @Test
    void emptyYieldsNoEffectsForEveryMode() {
        for (PotionApplicationMode mode : PotionApplicationMode.values()) {
            assertTrue(PotionEffectDefinition.EMPTY.getEffects(mode).isEmpty());
        }
    }

    @Test
    void ofScalesDurationPerMode() {
        PotionEffectDefinition def = PotionEffectDefinition.of(1200,
                duration -> List.of(new EffectSpeed().setDuration(duration)));

        assertEquals(1200, def.getEffects(PotionApplicationMode.DRINK).get(0).getDuration());
        assertEquals(300, def.getEffects(PotionApplicationMode.LINGERING).get(0).getDuration());
        assertEquals(150, def.getEffects(PotionApplicationMode.ARROW).get(0).getDuration());
    }

    @Test
    void supplierAccessorReturnsGivenFunction() {
        PotionEffectDefinition def = PotionEffectDefinition.of(600,
                duration -> List.of(new EffectSpeed().setDuration(duration)));
        assertNotNull(def.supplier());
    }
}
