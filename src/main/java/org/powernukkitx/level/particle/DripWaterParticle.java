package org.powernukkitx.level.particle;

import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class DripWaterParticle extends GenericParticle {
    public DripWaterParticle(Vector3 pos) {
        super(pos, ParticleType.DRIP_WATER);
    }
}