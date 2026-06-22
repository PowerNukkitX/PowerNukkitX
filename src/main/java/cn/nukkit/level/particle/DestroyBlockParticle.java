package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class DestroyBlockParticle extends GenericParticle {

    public DestroyBlockParticle(Vector3 pos, Block block) {
        super(pos, LevelEvent.PARTICLE_DESTROY_BLOCK, block.getRuntimeId());
    }
}