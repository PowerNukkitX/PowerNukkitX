package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.LevelEventType;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class GenericParticle extends Particle {

    protected final int data;
    protected final LevelEventType type;

    public GenericParticle(Vector3 pos, LevelEventType type) {
        this(pos, type, 0);
    }

    public GenericParticle(Vector3 pos, LevelEventType type, int data) {
        super(pos.x, pos.y, pos.z);
        this.type = type;
        this.data = data;
    }

    @Override
    public BedrockPacket[] encode() {
        final LevelEventPacket pk = new LevelEventPacket();
        pk.setType(this.type);
        pk.setPosition(Vector3f.from(this.x, this.y, this.z));
        pk.setData(this.data);
        return new LevelEventPacket[]{pk};
    }
}