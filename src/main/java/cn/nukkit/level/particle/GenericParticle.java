package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class GenericParticle extends Particle {

    protected final int data;
    protected int id = 0;

    public GenericParticle(Vector3 pos, int id) {
        this(pos, id, 0);
    }

    public GenericParticle(Vector3 pos, int id, int data) {
        super(pos.x, pos.y, pos.z);
        this.id = id;
        this.data = data;
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        LevelEvent[] values = LevelEvent.values();
        pk.setType((this.id >= 0 && this.id < values.length) ? values[this.id] : LevelEvent.UNDEFINED);
        pk.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        pk.setData(this.data);

        return new BedrockPacket[]{pk};
    }
}
