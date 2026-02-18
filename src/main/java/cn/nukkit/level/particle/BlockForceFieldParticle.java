package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3 pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_BLOCK_FORCE_FIELD);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.setType(LevelEvent.PARTICLE_DENY_BLOCK);
        pk.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        pk.setData(this.data);

        return new BedrockPacket[]{pk};
    }
}
