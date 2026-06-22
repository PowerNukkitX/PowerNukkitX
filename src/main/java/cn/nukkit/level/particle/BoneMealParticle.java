package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author CreeperFace
 * @since 15.4.2017
 */
public class BoneMealParticle extends GenericParticle {

    public BoneMealParticle(Vector3 pos) {
        super(pos, LevelEvent.PARTICLE_CROP_GROWTH);
    }
}