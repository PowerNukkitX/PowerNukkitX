package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class HappyVillagerParticle extends GenericParticle {
    public HappyVillagerParticle(Vector3 pos) {
        super(pos, ParticleType.VILLAGER_HAPPY);
    }
}
