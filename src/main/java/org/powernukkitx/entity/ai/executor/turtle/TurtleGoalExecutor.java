package org.powernukkitx.entity.ai.executor.turtle;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.block.property.enums.TurtleEggCount;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.event.entity.EntityBlockChangeEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.powernukkitx.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public final class TurtleGoalExecutor implements IBehaviorExecutor {
    public enum Goal { PANIC, WATER, HOME, LAY, LAND }

    private final Goal goal;
    private final Supplier<Vector3> homeTarget;
    private final Runnable onReachHome;
    private Vector3 target;
    private int nextSearch;
    private int diggingTicks;
    private int elapsed;
    private boolean goingHome;

    /**
     * Creates a goal that searches for a nearby block.
     *
     * @param goal the type of block search to perform
     * @throws IllegalArgumentException if the goal requires a home target
     */
    public TurtleGoalExecutor(@NotNull Goal goal) {
        this(goal, null, null);
        if (goal == Goal.HOME) throw new IllegalArgumentException("Home goal requires a target and arrival callback");
    }

    /**
     * Creates a goal that refreshes the birthplace target while traveling.
     *
     * @param homeTarget supplies the center of the birthplace block
     * @param onReachHome runs when the turtle is within four blocks of the target
     */
    public TurtleGoalExecutor(@NotNull Supplier<Vector3> homeTarget, @NotNull Runnable onReachHome) {
        this(Goal.HOME, homeTarget, onReachHome);
    }

    private TurtleGoalExecutor(Goal goal, Supplier<Vector3> homeTarget, Runnable onReachHome) {
        this.goal = goal;
        this.homeTarget = homeTarget;
        this.onReachHome = onReachHome;
    }

    /**
     * Rolls for a return to the birthplace and keeps the result until the goal stops.
     *
     * @return whether the return journey can run
     */
    public boolean canGoHome() {
        if (!goingHome && ThreadLocalRandom.current().nextInt(700) == 0) goingHome = true;
        return goingHome;
    }

    /**
     * Reuses the current target or searches for one when the search cooldown expires.
     *
     * @param entity the turtle running this goal
     * @return whether the goal has a valid target block
     */
    public boolean canStart(@NotNull EntityIntelligent entity) {
        if (target != null && validTarget(entity, target)) return true;
        target = null;
        resetDigging(entity);
        if (entity.level.getTick() < nextSearch) return false;
        nextSearch = entity.level.getTick() + 100;
        target = findTarget(entity);
        return target != null;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        elapsed++;
        if (goal == Goal.HOME) {
            if (target == null || elapsed % 40 == 0) target = homeTarget.get();
            if (entity.distanceSquared(target) <= 16) {
                onReachHome.run();
                return false;
            }
        } else {
            if (target != null && !validTarget(entity, target)) {
                target = null;
                resetDigging(entity);
            }
            if (target == null && !canStart(entity)) return false;
            double radius = goal == Goal.WATER ? (entity.isBaby() ? 0.1 : 1.5) : goal == Goal.LAY ? 1.5 : 0.5;
            if (entity.distanceSquared(target) <= radius * radius) {
                if (goal != Goal.LAY && (goal != Goal.WATER || entity.isInsideOfWater())) return false;
                if (goal == Goal.LAY && !entity.isInsideOfWater() && entity.isOnGround()) return dig(entity);
            }
            if (goal == Goal.LAY) resetDigging(entity);
        }
        if (goal != Goal.HOME && !entity.level.isChunkLoaded(target.getFloorX() >> 4, target.getFloorZ() >> 4)) return false;
        var finder = entity.getBehaviorGroup().getRouteFinder();
        if (goal != Goal.HOME && elapsed > 20 && finder.isFinished() && !finder.isReachable()
                && target.equals(finder.getTarget())) {
            nextSearch = entity.level.getTick() + 100;
            return false;
        }
        if (entity.getMoveTarget() == null || !entity.getMoveTarget().equals(target)) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
        }
        entity.setMoveTarget(target);
        entity.setLookTarget(target);
        entity.setMovementSpeed((entity.isInsideOfWater() ? entity.getDefaultUnderWaterSpeed() : 0.1f) * (goal == Goal.PANIC ? 1.2f : 1f));
        if (elapsed % 20 == 0) entity.getBehaviorGroup().setForceUpdateRoute(true);
        return true;
    }

    private boolean dig(EntityIntelligent entity) {
        entity.setMoveTarget(null);
        entity.setDataFlag(ActorFlags.LAYING_EGG, true);
        if (++diggingTicks % 5 == 0) {
            entity.level.addParticle(new DestroyBlockParticle(target, entity.level.getBlock(target.add(0, -1, 0))));
        }
        if (diggingTicks <= 200) return true;
        Block from = entity.level.getBlock(target);
        BlockTurtleEgg eggs = new BlockTurtleEgg();
        eggs.setEggCount(TurtleEggCount.values()[ThreadLocalRandom.current().nextInt(4)]);
        EntityBlockChangeEvent event = new EntityBlockChangeEvent(entity, from, eggs);
        entity.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && validTarget(entity, target) && entity.level.setBlock(target, eggs, true, true)) {
            entity.level.addSound(target, Sound.BLOCK_TURTLE_EGG_DROP);
            entity.setPregnant(false);
        }
        nextSearch = entity.level.getTick() + 100;
        return false;
    }

    private Vector3 findTarget(EntityIntelligent entity) {
        int range = goal == Goal.PANIC ? 10 : goal == Goal.WATER && entity.isBaby() ? 15 : 16;
        int height = goal == Goal.LAY || goal == Goal.PANIC ? 4 : 5;
        Vector3 best = null;
        double nearest = Double.MAX_VALUE;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                int bx = entity.getFloorX() + dx;
                int bz = entity.getFloorZ() + dz;
                if (!entity.level.isChunkLoaded(bx >> 4, bz >> 4)) continue;
                for (int dy = -height; dy <= height; dy++) {
                    double cy = entity.getFloorY() + dy;
                    double distance = (bx + 0.5 - entity.x) * (bx + 0.5 - entity.x)
                            + (cy - entity.y) * (cy - entity.y) + (bz + 0.5 - entity.z) * (bz + 0.5 - entity.z);
                    if (distance >= nearest) continue;
                    Vector3 candidate = new Vector3(bx + 0.5, cy, bz + 0.5);
                    if (validTarget(entity, candidate)) {
                        best = candidate;
                        nearest = distance;
                    }
                }
            }
        }
        return best;
    }

    private boolean validTarget(EntityIntelligent entity, Vector3 pos) {
        if (!entity.level.isChunkLoaded(pos.getFloorX() >> 4, pos.getFloorZ() >> 4)) return false;
        Block block = entity.level.getBlock(pos);
        if (goal == Goal.WATER || goal == Goal.PANIC) {
            if (!(block instanceof BlockFlowingWater)) return false;
            double dx = pos.x - entity.x;
            double dz = pos.z - entity.z;
            int stepX = Math.abs(dx) >= Math.abs(dz) ? (dx >= 0 ? 1 : -1) : 0;
            int stepZ = stepX == 0 ? (dz >= 0 ? 1 : -1) : 0;
            return entity.level.getBlock(pos.add(-stepX, 0, -stepZ), false) instanceof BlockFlowingWater;
        }
        if (!block.isAir() || !entity.level.getBlock(pos, 1).isAir()) return false;
        Block below = entity.level.getBlock(pos.add(0, -1, 0));
        return goal == Goal.LAY ? BlockID.SAND.equals(below.getId()) : below.isSolid();
    }

    private void resetDigging(EntityIntelligent entity) {
        diggingTicks = 0;
        if (goal == Goal.LAY) entity.setDataFlag(ActorFlags.LAYING_EGG, false);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        target = null;
        elapsed = 0;
        goingHome = false;
        resetDigging(entity);
        entity.setMoveTarget(null);
        entity.setLookTarget(null);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
