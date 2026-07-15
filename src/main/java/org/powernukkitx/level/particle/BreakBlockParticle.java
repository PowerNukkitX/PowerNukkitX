package org.powernukkitx.level.particle;

import org.powernukkitx.block.Block;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.LevelEventType;

public class BreakBlockParticle extends GenericParticle {

    public BreakBlockParticle(Vector3 pos, Block block, BlockFace face) {
        super(pos, typeFromFace(face), block.getRuntimeId());
    }

    private static LevelEventType typeFromFace(BlockFace face) {
        return switch (face) {
            case DOWN -> LevelEvent.PARTICLE_BREAK_BLOCK_DOWN;
            case UP -> LevelEvent.PARTICLE_BREAK_BLOCK_UP;
            case NORTH -> LevelEvent.PARTICLE_BREAK_BLOCK_NORTH;
            case SOUTH -> LevelEvent.PARTICLE_BREAK_BLOCK_SOUTH;
            case WEST -> LevelEvent.PARTICLE_BREAK_BLOCK_WEST;
            case EAST -> LevelEvent.PARTICLE_BREAK_BLOCK_EAST;
        };
    }
}
