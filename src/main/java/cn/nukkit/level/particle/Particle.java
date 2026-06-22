package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Particle extends Vector3 {

    public Particle() {
        super(0, 0, 0);
    }

    public Particle(double x) {
        super(x, 0, 0);
    }

    public Particle(double x, double y) {
        super(x, y, 0);
    }

    public Particle(double x, double y, double z) {
        super(x, y, z);
    }

    abstract public BedrockPacket[] encode();
}
