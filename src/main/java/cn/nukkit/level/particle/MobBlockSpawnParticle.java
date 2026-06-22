package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class MobBlockSpawnParticle extends GenericParticle {

    public MobBlockSpawnParticle(Vector3 pos, float width, float height) {
        super(pos, LevelEvent.PARTICLE_MOB_BLOCK_SPAWN, ((int) width & 0xff) + (((int) height & 0xff) << 8));
    }
}