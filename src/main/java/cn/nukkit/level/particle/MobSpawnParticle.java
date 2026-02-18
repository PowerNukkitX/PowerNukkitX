package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class MobSpawnParticle extends Particle {

    protected final int width;
    protected final int height;

    public MobSpawnParticle(Vector3 pos, float width, float height) {
        super(pos.x, pos.y, pos.z);
        this.width = (int) width;
        this.height = (int) height;
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEvent.PARTICLE_MOB_BLOCK_SPAWN);
        packet.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        packet.setData((this.width & 0xff) + ((this.height & 0xff) << 8));

        return new BedrockPacket[]{packet};
    }
}
