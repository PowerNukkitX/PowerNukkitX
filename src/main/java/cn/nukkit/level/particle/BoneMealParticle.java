package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author CreeperFace
 * @since 15.4.2017
 */
public class BoneMealParticle extends Particle {

    public BoneMealParticle(Vector3 pos) {
        super(pos.x, pos.y, pos.z);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.setType(LevelEvent.PARTICLE_CROP_GROWTH);
        pk.setPosition(Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        pk.setData(0);

        return new BedrockPacket[]{pk};
    }
}
