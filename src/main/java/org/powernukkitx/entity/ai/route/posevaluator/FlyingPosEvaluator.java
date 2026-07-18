package org.powernukkitx.entity.ai.route.posevaluator;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;


public class FlyingPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 vec) {
        //Check whether it is reachable
        return isPassable(entity, vec);
    }

    /**
     * Whether the given entity can occupy the given coordinate without colliding.
     * Specially optimized for movement through open space.
     */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) * 0.5 + 0.1;
        float height = entity.getHeight() * entity.getScale();
        // In vanilla, entities do not fly along the ground
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY() - height * 0.5, vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
