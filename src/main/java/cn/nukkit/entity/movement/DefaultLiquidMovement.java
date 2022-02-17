package cn.nukkit.entity.movement;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public interface DefaultLiquidMovement extends Movement {
    @Override
    default void handleLiquidMovement() {
        final var entity = getSelf();
        final var tmp = new Vector3();
        BlockLiquid blockLiquid = null;
        for (final var each : entity.getLevel().getCollisionBlocks(getOffsetBoundingBox(),
                false, true, block -> block instanceof BlockLiquid)) {
            blockLiquid = (BlockLiquid) each;
            final var flowVector = blockLiquid.getFlowVector();
            tmp.x += flowVector.x;
            tmp.y += flowVector.y;
            tmp.z += flowVector.z;
        }
        if(blockLiquid != null) {
            final var len = tmp.length();
            final var speed = getLiquidMovementSpeed(blockLiquid) * 0.3f;
            if(len > 0){
                entity.motionX += tmp.x / len * speed;
                entity.motionY += tmp.y / len * speed;
                entity.motionZ += tmp.z / len * speed;
            }
            handleFloatingMovement();
        }
    }

    default void handleFloatingMovement() {
        final var entity = getSelf();
        if(entity.isInsideOfWater()) {
            entity.motionY += 0.1f;
        }
    }

    default float getLiquidMovementSpeed(BlockLiquid liquid) {
        if (liquid instanceof BlockLava) {
            return 0.02f;
        }
        return 0.05f;
    }
}
