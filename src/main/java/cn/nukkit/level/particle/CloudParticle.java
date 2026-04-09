package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;


public class CloudParticle extends GenericParticle {

    public CloudParticle(Vector3 pos) {
        this(pos, 0);
    }

    public CloudParticle(Vector3 pos, int scale) {
        super(pos, ParticleType.EVAPORATION, scale);
    }
}
