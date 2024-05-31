package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class CriticalParticle extends GenericParticle {
    /**
     * @deprecated 
     */
    
    public CriticalParticle(Vector3 pos) {
        this(pos, 2);
    }
    /**
     * @deprecated 
     */
    

    public CriticalParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_CRIT, scale);
    }
}
