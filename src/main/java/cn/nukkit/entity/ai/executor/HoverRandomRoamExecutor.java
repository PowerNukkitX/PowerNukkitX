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

    private void applyHoverSpeed(EntityIntelligent entity) {
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

            int surfaceY = findSurfaceBelow(level, colX, colZ, entity.getFloorY() - 1);
            if (surfaceY < level.getMinHeight()) {
                continue;
            }

            double minFeetY = surfaceY + 1.0 + hoverMin + yOffset;
            double maxFeetY = surfaceY + 1.0 + hoverMax + yOffset;
            double stepLimit = Math.max(0.4, Math.min(1.25, yDist * 0.35));
            double maxStep = Math.max(0.4, Math.min(stepLimit, (maxFeetY - minFeetY) * 0.5));
            double minStep = Math.min(0.35, maxStep);
            double deltaY = rnd.nextDouble(minStep, maxStep) * (rnd.nextBoolean() ? 1 : -1);
            double feetY = entity.y + deltaY;
            if (feetY < minFeetY) feetY = Math.min(maxFeetY, minFeetY + rnd.nextDouble(0.0, maxStep));
            if (feetY > maxFeetY) feetY = Math.max(minFeetY, maxFeetY - rnd.nextDouble(0.0, maxStep));
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