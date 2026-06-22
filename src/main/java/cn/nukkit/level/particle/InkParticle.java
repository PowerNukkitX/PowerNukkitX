package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class InkParticle extends GenericParticle {

    public InkParticle(Vector3 pos) {
        this(pos, 0);
    }

    public InkParticle(Vector3 pos, int scale) {
        super(pos, ParticleType.INK, scale);
    }
}
