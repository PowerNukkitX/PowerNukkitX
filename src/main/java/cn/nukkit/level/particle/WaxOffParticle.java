package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author joserobjr
 * @since 2021-06-14
 */

public class WaxOffParticle extends GenericParticle {

    public WaxOffParticle(Vector3 pos) {
        super(pos, Particle.TYPE_WAX);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.setType(LevelEvent.PARTICLE_WAX_OFF);
        pk.setPosition(Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        pk.setData(this.data);

        return new BedrockPacket[]{pk};
    }
}
