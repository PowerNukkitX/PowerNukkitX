package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class LavaParticle extends GenericParticle {
    /**
     * @deprecated 
     */
    
    public LavaParticle(Vector3 pos) {
        super(pos, Particle.TYPE_LAVA);
    }
}
