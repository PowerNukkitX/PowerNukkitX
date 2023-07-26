package cn.nukkit.level;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.Test;

/**
 * @author joserobjr
 * @since 2021-12-13
 */
@PowerNukkitOnly
@Since("FUTURE")
class ParticleEffectTest {
    @Test
    void getIdentifier() {
        assertEquals("minecraft:wax_particle", ParticleEffect.WAX.getIdentifier());
    }
}
