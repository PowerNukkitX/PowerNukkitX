package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class ItemBreakParticle extends Particle {

    private final int data;

    public ItemBreakParticle(Vector3 pos, Item item) {
        super(pos.x, pos.y, pos.z);
        this.data = (item.getRuntimeId() << 16 | item.getDamage());
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEvent.PARTICLE_CRACK_BLOCK);
        packet.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        packet.setData(this.data);
        return new BedrockPacket[]{packet};
    }
}
