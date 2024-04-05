package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

public class PunchBlockParticle extends Particle {

    protected final int data;

    public PunchBlockParticle(Vector3 pos, Block block) {
        super(pos.x, pos.y, pos.z);
        this.data = block.getBlockState().blockStateHash();
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_CRACK_BLOCK;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = this.data;

        return new DataPacket[]{pk};
    }
}
