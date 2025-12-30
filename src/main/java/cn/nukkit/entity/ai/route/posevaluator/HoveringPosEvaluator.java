package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class HoveringPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 vec) {
        return isPassable(entity, vec);
    }

    /** Check if the entity can stand/fly at the given feet position without collisions. */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vec) {
        double radius = (entity.getWidth() * entity.getScale()) * 0.5 + 0.1;
        float height = entity.getHeight() * entity.getScale();


        double feetY = vec.getY();
        double minY = feetY + 0.01; // Small epsilon to don't "hug" at the block edge
        double maxY = feetY + height;

        AxisAlignedBB bb = new SimpleAxisAlignedBB(
                vec.getX() - radius,
                minY,
                vec.getZ() - radius,
                vec.getX() + radius,
                maxY,
                vec.getZ() + radius
        );

        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
