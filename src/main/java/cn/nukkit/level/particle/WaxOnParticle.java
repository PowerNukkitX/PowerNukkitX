package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
public class WaxOnParticle extends GenericParticle {

    public WaxOnParticle(Vector3 pos) {
        super(pos, LevelEvent.PARTICLE_WAX_ON);
    }
}