package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * @author xtypr
 * @since 2015/12/27
 * The name "spell" comes from minecraft wiki.
 */
public class SpellParticle extends Particle {

    protected final int data;

    public SpellParticle(Vector3 pos) {
        this(pos, 0);
    }

    public SpellParticle(Vector3 pos, int data) {
        super(pos.x, pos.y, pos.z);
        this.data = data;
    }

    public SpellParticle(Vector3 pos, BlockColor blockColor) {
        //alpha is ignored
        this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue());
    }

    public SpellParticle(Vector3 pos, int r, int g, int b) {
        this(pos, r, g, b, 0x00);
    }

    protected SpellParticle(Vector3 pos, int r, int g, int b, int a) {
        this(pos, ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.setType(LevelEvent.PARTICLE_POTION_SPLASH);
        pk.setPosition(org.cloudburstmc.math.vector.Vector3f.from((float) this.x, (float) this.y, (float) this.z));
        pk.setData(this.data);

        return new BedrockPacket[]{pk};
    }
}
