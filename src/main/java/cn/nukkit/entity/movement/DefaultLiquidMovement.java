package cn.nukkit.entity.movement;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public interface DefaultLiquidMovement extends Movement {
    @Override
    default void handleLiquidMovement() {
        final Entity entity = getSelf();
        final Vector3 tmp = new Vector3();
        BlockLiquid blockLiquid = null;
        for (final Block each : entity.getLevel().getCollisionBlocks(getOffsetBoundingBox(),
                false, true, block -> block instanceof BlockLiquid)) {
            blockLiquid = (BlockLiquid) each;
            final Vector3 flowVector = blockLiquid.getFlowVector();
            tmp.x += flowVector.x;
            tmp.y += flowVector.y;
            tmp.z += flowVector.z;
        }
        if(blockLiquid != null) {
            final double len = tmp.length();
            final float speed = getLiquidMovementSpeed(blockLiquid) * 0.3f;
            if(len > 0){
                entity.motionX += tmp.x / len * speed;
                entity.motionY += tmp.y / len * speed;
                entity.motionZ += tmp.z / len * speed;
            }
            handleFloatingMovement();
        }
    }

    default void handleFloatingMovement() {

    }

    default float getLiquidMovementSpeed(BlockLiquid liquid) {
        if (liquid instanceof BlockLava) {
            return 0.02f;
        }
        return 0.05f;
    }
}
