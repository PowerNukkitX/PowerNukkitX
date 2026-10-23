package org.powernukkitx.entity.ai.route.posevaluator;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;

/** Allows flight and landing with the parrot's feet above safe solid surfaces. */
public class ParrotPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 position) {
        if (position.y <= entity.level.getMinHeight() || position.y + entity.getHeight() >= entity.level.getMaxHeight()) return false;
        double radius = entity.getWidth() * entity.getScale() * 0.5;
        var box = new SimpleAxisAlignedBB(position.x - radius, position.y, position.z - radius,
                position.x + radius, position.y + entity.getHeight() * entity.getScale(), position.z + radius);
        if (!entity.level.isAreaLoaded(box)) return false;
        for (int y = position.getFloorY(); y <= (int) Math.floor(box.getMaxY()); y++) {
            for (int x = (int) Math.floor(box.getMinX()); x <= (int) Math.floor(box.getMaxX()); x++) {
                for (int z = (int) Math.floor(box.getMinZ()); z <= (int) Math.floor(box.getMaxZ()); z++) {
                    Block block = entity.level.getTickCachedBlock(x, y, z, 0, false);
                    Block secondary = entity.level.getTickCachedBlock(x, y, z, 1, false);
                    if (block == null || secondary == null || dangerous(block.getId()) || dangerous(secondary.getId())) return false;
                }
            }
        }
        Block below = entity.level.getTickCachedBlock(position.add(0, -1, 0), false);
        return below != null && !damaging(below.getId()) && !Utils.hasCollisionTickCachedBlocks(entity.level, box);
    }

    private boolean dangerous(String id) {
        return id.equals(Block.WATER) || id.equals(Block.FLOWING_WATER)
                || id.equals(Block.LAVA) || id.equals(Block.FLOWING_LAVA) || damaging(id);
    }

    private boolean damaging(String id) {
        return id.equals(Block.FIRE) || id.equals(Block.SOUL_FIRE) || id.equals(Block.CACTUS)
                || id.equals(Block.MAGMA) || id.equals(Block.CAMPFIRE) || id.equals(Block.SOUL_CAMPFIRE)
                || id.equals(Block.SWEET_BERRY_BUSH);
    }
}
