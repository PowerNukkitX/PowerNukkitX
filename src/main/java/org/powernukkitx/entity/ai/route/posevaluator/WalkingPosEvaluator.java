package org.powernukkitx.entity.ai.route.posevaluator;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFence;
import org.powernukkitx.block.BlockFenceGate;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Block evaluator for standard land-walking entities
 */


public class WalkingPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        //Centered coordinate
        var blockCenter = block.add(0.5, 1, 0.5);
        //Check whether it is reachable
        if (!isPassable(entity, blockCenter))
            return false;
        if (entity.hasWaterAt(0) && blockCenter.getY() - entity.getY() > 1)//An entity in water can't move to a block more than one block high
            return false;
        //TODO: check for head collision
        //The block underfoot must not be a damaging block
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.LAVA || block.getId() == Block.CACTUS)
            return false;
        //Must not be a fence
        if (block instanceof BlockFence || block instanceof BlockFenceGate)
            return false;
        //Special case for water
        if (block.getId() == Block.WATER || block.getId() == Block.FLOWING_WATER)
            return true;
        //Must be standable
        return !block.canPassThrough();
    }

    /**
     * Determines whether the entity can occupy the given position without colliding.
     */
    //todo: this method is very expensive due to the collision check, needs optimization
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        if (radius > 0.5) {
            // A --- B --- C
            // |           |
            // D     P     E
            // |           |
            // F --- G --- H
            // Passing through at point P in one go is most likely, so check it first
            byte collisionInfo = Utils.hasCollisionTickCachedBlocksWithInfo(entity.level, bb);
            if (collisionInfo == 0) {
                return true;
            }
            // Align the entity's collision box to A B C D E F G H respectively and check whether it can pass
            var dr = radius - 0.5;
            for (int i = -1; i <= 1; i++) {
                // collisionInfo & 0b110000: get the x-axis collision info, 3 means collision on the side greater than center, 1 means collision on the side less than center, 0 means no collision
                // -2: converts 3 to 1, 1 to -1, and 0 to -2
                // then check: if i equals the value above, that direction is 100% going to collide, so no need to check further
                if (((collisionInfo & 0b110000) >> 4) - 2 == i) continue;
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // point P has already been checked
                    if ((collisionInfo & 0b000011) - 2 == j) continue; // get the z-axis collision info and compare
                    // since the blocks are already cached, the check speed is still acceptable
                    if (!Utils.hasCollisionTickCachedBlocks(entity.level, bb.clone().offset(i * dr, 0, j * dr))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
        }
    }
}
