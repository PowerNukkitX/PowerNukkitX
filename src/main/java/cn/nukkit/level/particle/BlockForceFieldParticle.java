package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3 pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3 pos, int scale) {
        super(pos, ParticleType.BLOCK_FORCE_FIELD);
    }
}