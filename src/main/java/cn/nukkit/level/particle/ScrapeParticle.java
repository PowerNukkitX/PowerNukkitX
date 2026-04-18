package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
public class ScrapeParticle extends GenericParticle {

    public ScrapeParticle(Vector3 pos) {
        super(pos, LevelEvent.PARTICLE_SCRAPE);
    }
}