package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class MobSpellInstantaneousParticle extends GenericParticle {
    public MobSpellInstantaneousParticle(Vector3 pos) {
        super(pos, ParticleType.MOB_SPELL_INSTANTANEOUS);
    }
}
