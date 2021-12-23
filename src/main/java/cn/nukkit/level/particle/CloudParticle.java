package cn.nukkit.level.particle;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
public class CloudParticle extends GenericParticle {
    @PowerNukkitOnly
    public CloudParticle(Vector3 pos) {
        this(pos, 0);
    }

    @PowerNukkitOnly
    public CloudParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_EVAPORATION, scale);
    }
}
