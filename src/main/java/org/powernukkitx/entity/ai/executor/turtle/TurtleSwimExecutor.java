package org.powernukkitx.entity.ai.executor.turtle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

public final class TurtleSwimExecutor extends SpaceRandomRoamExecutor {
    private final float speedMultiplier;
    private final SwimmingPosEvaluator swimPositions = new SwimmingPosEvaluator();

    /**
     * Creates a roaming behavior for a turtle already in water.
     *
     * @param panicking whether to use the faster, shorter-range swim pattern
     */
    public TurtleSwimExecutor(boolean panicking) {
        super(panicking ? 0.144f : 0.12f, panicking ? 5 : 30, panicking ? 4 : 15,
                panicking ? 40 : Integer.MAX_VALUE - 1, true, panicking ? 100 : 160, false, 10);
        speedMultiplier = panicking ? 1.2f : 1;
    }

    @Override
    protected boolean needUpdateTarget(EntityIntelligent entity) {
        return super.needUpdateTarget(entity) || entity.distanceSquared(entity.getMoveTarget()) <= 1;
    }

    @Override
    protected @Nullable Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        for (int i = 0; i < 10; i++) {
            int x = entity.getFloorX() + random.nextInt(-maxRoamRange, maxRoamRange + 1);
            int z = entity.getFloorZ() + random.nextInt(-maxRoamRange, maxRoamRange + 1);
            if (!entity.level.isChunkLoaded(x >> 4, z >> 4)) continue;
            for (int dy = 0; dy <= maxYRoamRange * 2; dy++) {
                int y = entity.getFloorY() + (dy % 2 == 0 ? dy / 2 : -(dy + 1) / 2);
                Vector3 pos = new Vector3(x + 0.5, y + 0.5, z + 0.5);
                if (isSwimTarget(entity, pos)) return pos;
            }
        }
        Vector3 target = null;
        int count = 0;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    Vector3 pos = new Vector3(entity.getFloorX() + dx + 0.5,
                            entity.getFloorY() + dy + 0.5, entity.getFloorZ() + dz + 0.5);
                    if (isSwimTarget(entity, pos) && random.nextInt(++count) == 0) target = pos;
                }
            }
        }
        return target;
    }

    private boolean isSwimTarget(EntityIntelligent entity, Vector3 pos) {
        return entity.distanceSquared(pos) >= 1
                && entity.level.isChunkLoaded(pos.getFloorX() >> 4, pos.getFloorZ() >> 4)
                && entity.level.getBlock(pos, false) instanceof BlockFlowingWater
                && swimPositions.evalPos(entity, pos);
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        durationTick++;
        if (needUpdateTarget(entity) || durationTick > runningTime) {
            Vector3 target = next(entity);
            if (target == null) {
                stop(entity);
                return false;
            }
            setRouteTarget(entity, target);
            setLookTarget(entity, target);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            durationTick = 0;
        }
        entity.setMovementSpeed(entity.getDefaultUnderWaterSpeed() * speedMultiplier);
        return true;
    }
}
