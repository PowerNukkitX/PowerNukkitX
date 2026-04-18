package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class CriticalParticle extends GenericParticle {
    public CriticalParticle(Vector3 pos) {
        this(pos, 2);
    }

    public CriticalParticle(Vector3 pos, int scale) {
        super(pos, ParticleType.CRIT, scale);
    }
}
