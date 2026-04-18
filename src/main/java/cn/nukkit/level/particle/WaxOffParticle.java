package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
public class WaxOffParticle extends GenericParticle {

    public WaxOffParticle(Vector3 pos) {
        super(pos, LevelEvent.PARTICLE_WAX_OFF);
    }
}