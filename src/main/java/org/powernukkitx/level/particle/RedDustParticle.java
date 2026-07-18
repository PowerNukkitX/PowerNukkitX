package org.powernukkitx.level.particle;

import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class RedDustParticle extends GenericParticle {
    public RedDustParticle(Vector3 pos) {
        this(pos, 1);
    }

    public RedDustParticle(Vector3 pos, int lifetime) {
        super(pos, ParticleType.RED_DUST, lifetime);
    }
}
