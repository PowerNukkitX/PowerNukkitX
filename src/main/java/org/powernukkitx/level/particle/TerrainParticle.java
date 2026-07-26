package org.powernukkitx.level.particle;

import org.powernukkitx.block.Block;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3 pos, Block block) {
        super(pos, ParticleType.TERRAIN, block.getRuntimeId());
    }
}