package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Allows the mob to hover around randomly, close to the surface.
 * Requires SpaceMoveController + LiftController + SimpleSpaceAStarRouteFinder (HoveringPosEvaluator).
 */
public class HoverRandomRoamExecutor implements IBehaviorExecutor, EntityControl {
    private final float speedMultiplier;
    private final int xzDist;
    private final int yDist;
    private final double yOffset;
    private final double hoverMin;
    private final double hoverMax;
    private final int interval;

    private int ticks = 0;

    public HoverRandomRoamExecutor(
            float speedMultiplier,
            int xzDist,
            int yDist,
            double yOffset,
            double hoverMin,
            double hoverMax,
            int interval
    ) {
        this.speedMultiplier = speedMultiplier;
        this.xzDist = Math.max(1, xzDist);
        this.yDist = Math.max(1, yDist);
        this.yOffset = yOffset;
        this.hoverMin = hoverMin;
        this.hoverMax = Math.max(hoverMin, hoverMax);
        this.interval = Math.max(1, interval);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, true);
        entity.setEnablePitch(false);
        ticks = 0;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    private void stop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, false);
        entity.setEnablePitch(true);
        ticks = 0;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        ticks++;

        if (entity.isEnablePitch()) { entity.setEnablePitch(false); }
        if (correctHoverBand(entity)) { return true; }

        Vector3 currentTarget = entity.getMoveTarget();
        boolean reached = currentTarget != null && entity.distanceSquared(currentTarget) <= 2.25;
        boolean mustPick = currentTarget == null || reached || ticks >= interval * 3;
        if (!mustPick) { return true; }

        ticks = 0;
        pickNewTarget(entity);
        return true;
    }

    private boolean correctHoverBand(EntityIntelligent entity) {
        Level level = entity.level;
        if (level == null) {
            return false;
        }

        int surfaceY = findSurfaceBelow(level, entity.getFloorX(), entity.getFloorZ(), entity.getFloorY() - 1);
        if (surfaceY < level.getMinHeight()) {
            return false;
        }

        double maxFeetY = surfaceY + 1.0 + hoverMax + yOffset;
        if (entity.y <= maxFeetY) {
            entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, true);
            return false;
        }

        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, false);
        if (entity.motionY > 0) {
            entity.motionY = 0;
        }

        Vector3 currentTarget = entity.getMoveTarget();
        Vector3 target = currentTarget != null ? new Vector3(currentTarget.x, maxFeetY, currentTarget.z) : new Vector3(entity.x, maxFeetY, entity.z);
        applyHoverSpeed(entity);
        setRouteTarget(entity, target);
        setLookTarget(entity, target);
        entity.getBehaviorGroup().setForceUpdateRoute(true);
        ticks = 0;
        return true;
    }

    private void applyHoverSpeed(EntityIntelligent entity) {
        float speed = entity.getDefaultFlyingSpeed() * speedMultiplier;
        if (entity.getMovementSpeed() != speed) {
            entity.setMovementSpeed(speed);
        }
    }

    private void pickNewTarget(EntityIntelligent entity) {
        Level level = entity.level;
        if (level == null) {
            return;
        }

        final ThreadLocalRandom rnd = ThreadLocalRandom.current();

        final int baseEx = entity.getFloorX();
        final int baseEz = entity.getFloorZ();

        final float height = entity.getHeight() * entity.getScale();
        final double radius = (entity.getWidth() * entity.getScale()) * 0.5 + 0.1;

        for (int tries = 0; tries < 12; tries++) {

            // Random horizontal column around the mob
            int colX = baseEx + rnd.nextInt(-xzDist, xzDist + 1);
            int colZ = baseEz + rnd.nextInt(-xzDist, xzDist + 1);
            int moveX = colX - baseEx;
            int moveZ = colZ - baseEz;
            int minTravel = Math.max(2, xzDist / 3);
            if (tries < 8 && moveX * moveX + moveZ * moveZ < minTravel * minTravel) {
                continue;
            }

            int surfaceY = findSurfaceBelow(level, colX, colZ, entity.getFloorY() - 1);
            if (surfaceY < level.getMinHeight()) {
                continue;
            }

            double minFeetY = surfaceY + 1.0 + hoverMin + yOffset;
            double maxFeetY = surfaceY + 1.0 + hoverMax + yOffset;
            double yStep = Math.max(0.75, Math.min(yDist * 0.25, 3.0));
            double minCandidateY = Math.max(minFeetY, entity.y - yStep);
            double maxCandidateY = Math.min(maxFeetY, entity.y + yStep);
            double feetY = minCandidateY < maxCandidateY ? rnd.nextDouble(minCandidateY, maxCandidateY) : minCandidateY;
            double horizontalDist = Math.sqrt(moveX * moveX + moveZ * moveZ);
            double maxSlopeY = Math.max(0.75, horizontalDist * 0.45);
            feetY = Math.max(entity.y - maxSlopeY, Math.min(entity.y + maxSlopeY, feetY));
            double centerY = feetY + height * 0.5;

            // World Y bounds
            if (feetY < level.getMinHeight() || centerY >= level.getMaxHeight() - 2) {
                continue;
            }

            Vector3 target = new Vector3(colX + 0.5, feetY, colZ + 0.5);

            AxisAlignedBB bb = new SimpleAxisAlignedBB(
                    target.getX() - radius,
                    target.getY() + 0.01,
                    target.getZ() - radius,
                    target.getX() + radius,
                    target.getY() + height,
                    target.getZ() + radius
            );

            if (Utils.hasCollisionTickCachedBlocks(entity.level, bb)) {
                continue;
            }

            // Apply target destination
            applyHoverSpeed(entity);
            setRouteTarget(entity, target);
            setLookTarget(entity, target);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            return;
        }

        Vector3 fallback = entity.getLocation();
        applyHoverSpeed(entity);
        setRouteTarget(entity, fallback);
        setLookTarget(entity, fallback);
        entity.getBehaviorGroup().setForceUpdateRoute(true);
    }

    private int findSurfaceBelow(Level level, int x, int z, int startY) {
        int maxY = Math.min(level.getMaxHeight() - 2, startY);
        for (int y = maxY; y >= level.getMinHeight(); y--) {
            if (!level.getTickCachedBlock(x, y, z).canPassThrough() && level.getTickCachedBlock(x, y + 1, z).canPassThrough()) {
                return y;
            }
        }
        return level.getMinHeight() - 1;
    }
}