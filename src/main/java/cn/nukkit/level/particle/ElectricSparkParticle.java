package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
public class ElectricSparkParticle extends GenericParticle {

    public ElectricSparkParticle(Vector3 pos) {
        super(pos, ParticleType.ELECTRIC_SPARK);
    }
}