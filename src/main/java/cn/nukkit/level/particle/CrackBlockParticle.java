package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;

public class CrackBlockParticle extends GenericParticle {

    public CrackBlockParticle(Vector3 pos, Block block) {
        super(pos, LevelEvent.PARTICLE_CRACK_BLOCK, block.getBlockState().blockStateHash());
    }
}