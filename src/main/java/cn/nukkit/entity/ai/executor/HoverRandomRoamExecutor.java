package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Allows the mob to hover around randomly, close to the surface.
 * Requires SpaceMoveController + LiftController + SimpleSpaceAStarRouteFinder(HoveringPosEvaluator).
 */
public class HoverRandomRoamExecutor implements IBehaviorExecutor, EntityControl {

    private final float speed;
    private final int xzDist;
    private final int yDist;
    private final double yOffset;
    private final double hoverMin;
    private final double hoverMax;
    private final int interval;

    private int ticks = 0;

    public HoverRandomRoamExecutor(
            float speed,
            int xzDist,
            int yDist,
            double yOffset,
            double hoverMin,
            double hoverMax,
            int interval
    ) {
        this.speed = speed;
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

        boolean mustPick = ticks >= interval || entity.getMoveTarget() == null;
        if (!mustPick) { return true; }

        ticks = 0;
        pickNewTarget(entity);
        return true;
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

            int surfaceY = level.getHighestBlockAt(colX, colZ);
            if (surfaceY <= 0) {
                continue;
            }

            // Vertical band + yDist jitter
            final double hoverFeetBase;
            if (hoverMax <= hoverMin) {
                hoverFeetBase = hoverMin;
            } else {
                hoverFeetBase = hoverMin + rnd.nextDouble() * (hoverMax - hoverMin);
            }

            int extraFeet = (yDist > 0) ? rnd.nextInt(-yDist, yDist + 1) : 0;
            double hoverFeet = hoverFeetBase + extraFeet;

            // Clamp to band
            if (hoverFeet < hoverMin) hoverFeet = hoverMin;
            if (hoverFeet > hoverMax) hoverFeet = hoverMax;

            double feetY = surfaceY + 1.0 + hoverFeet + yOffset;
            double centerY = feetY + height * 0.5;

            // World Y bounds
            if (feetY < level.getMinHeight() || centerY >= level.getMaxHeight() - 2) {
                continue;
            }

            Vector3 target = new Vector3(colX + 0.5, centerY, colZ + 0.5);

            AxisAlignedBB bb = new SimpleAxisAlignedBB(
                    target.getX() - radius,
                    target.getY() - height * 0.5,
                    target.getZ() - radius,
                    target.getX() + radius,
                    target.getY() + height,
                    target.getZ() + radius
            );

            if (Utils.hasCollisionTickCachedBlocks(entity.level, bb)) {
                continue;
            }

            // Apply target destination
            entity.setMovementSpeed(speed);
            setRouteTarget(entity, target);
            setLookTarget(entity, target);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            return;
        }

        // Fallback nudge up so it can eventually get unstuck
        Vector3 fallback = entity.add(0, Math.max(1.0, hoverMin), 0);
        entity.setMovementSpeed(speed);
        setRouteTarget(entity, fallback);
        setLookTarget(entity, fallback);
        entity.getBehaviorGroup().setForceUpdateRoute(true);
    }
}
