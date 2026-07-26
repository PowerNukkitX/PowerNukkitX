package org.powernukkitx.level.particle;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;

public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3 pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3 pos, int scale) {
        super(pos, ParticleType.BLOCK_FORCE_FIELD, scale);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.setType(LevelEvent.PARTICLE_DENY_BLOCK);
        pk.setPosition(Vector3f.from(x, y, z));
        pk.setData(data);
        return new BedrockPacket[]{pk};
    }
}
