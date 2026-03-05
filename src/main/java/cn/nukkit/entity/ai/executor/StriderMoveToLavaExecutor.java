package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.passive.EntityStrider;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Behavior that makes a Strider move toward nearby lava.
 *
 * Uses a stored memory position as a reference, finds adjacent lava blocks,
 * and routes the entity toward the lava center until it is standing on it.
 * Optionally enforces follow range checks and clears the memory when the
 * target is lost.
 * 
 * @author Curse
 */
@Getter
public class StriderMoveToLavaExecutor implements EntityControl, IBehaviorExecutor {
    protected final MemoryType<? extends Vector3> memory;
    protected final float speed;
    protected Vector3 oldTargetFloor;

    protected final boolean enableRangeTest;
    protected final float maxFollowRangeSquared;
    protected final float minFollowRangeSquared;

    protected final boolean clearDataWhenLose;

    public StriderMoveToLavaExecutor(MemoryType<? extends Vector3> memory, float speed) {
        this(memory, speed, -1, -1, false);
    }

    public StriderMoveToLavaExecutor(MemoryType<? extends Vector3> memory, float speed, float maxFollowRange, float minFollowRange, boolean clearDataWhenLose) {
        this.memory = memory;
        this.speed = speed;

        if (maxFollowRange >= 0 && minFollowRange >= 0) {
            this.maxFollowRangeSquared = maxFollowRange * maxFollowRange;
            this.minFollowRangeSquared = minFollowRange * minFollowRange;
            this.enableRangeTest = true;
        } else {
            this.maxFollowRangeSquared = -1;
            this.minFollowRangeSquared = -1;
            this.enableRangeTest = false;
        }

        this.clearDataWhenLose = clearDataWhenLose;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        var mem = entity.getBehaviorGroup().getMemoryStorage();
        if (mem.isEmpty(memory)) return false;

        Vector3 raw = mem.get(memory);
        if (raw == null) return false;
        raw = raw.clone();

        if (raw instanceof Position p && p.level != null && p.level != entity.level) return false;

        Vector3 standBlock = raw.floor();

        Block lavaBlock = findAdjacentLavaBlock(entity, (int) standBlock.x, (int) standBlock.y, (int) standBlock.z);
        if (lavaBlock == null) {
            stop(entity);
            return false;
        }

        Vector3 lavaCenter = new Vector3(lavaBlock.getFloorX() + 0.5f, lavaBlock.getFloorY(), lavaBlock.getFloorZ() + 0.5f);

        if (enableRangeTest) {
            double d2 = lavaCenter.distanceSquared(entity);
            if (d2 > maxFollowRangeSquared || d2 < minFollowRangeSquared) return false;
        }

        if (isCenterOnLava(entity)) {
            removeRouteTarget(entity);
            oldTargetFloor = null;

            if (clearDataWhenLose && entity.getBehaviorGroup() != null) {
                entity.getBehaviorGroup().getMemoryStorage().clear(memory);
            }
            return false;
        }

        setRouteTarget(entity, lavaCenter);
        setLookTarget(entity, lavaCenter.add(0, 1, 0));

        double dx = lavaCenter.x - entity.x;
        double dz = lavaCenter.z - entity.z;
        double d2xz = (dx * dx) + (dz * dz);

        if (!isCenterOnLava(entity) && d2xz <= (1.25 * 1.25)) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            nudgeInto(entity, lavaCenter, 0.06f);
        }

        Vector3 floor = lavaCenter.floor();
        if (oldTargetFloor == null || !oldTargetFloor.equals(floor)) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            oldTargetFloor = floor;
        }

        if (entity.getMovementSpeed() != speed) {
            entity.setMovementSpeed(speed);
        }

        return true;
    }

    protected Block findAdjacentLavaBlock(EntityIntelligent entity, int x, int y, int z) {
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        for (int dy = 0; dy >= -1; dy--) {
            int yy = y + dy;

            for (int[] d : dirs) {
                Block b = entity.level.getBlock(x + d[0], yy, z + d[1]);
                if (b == null) continue;
                if (b instanceof BlockFlowingLava) return b;
            }
        }

        return null;
    }

    protected boolean isCenterOnLava(EntityIntelligent entity) {
        int x = (int) Math.floor(entity.x);
        int z = (int) Math.floor(entity.z);

        int y0 = (int) Math.floor(entity.y - 0.10);
        int y1 = (int) Math.floor(entity.y - 0.90);

        Block b0 = entity.level.getBlock(x, y0, z);
        if (b0 != null) return (b0 instanceof BlockFlowingLava);

        Block b1 = entity.level.getBlock(x, y1, z);
        if (b1 != null) return (b1 instanceof BlockFlowingLava);

        return false;
    }

    protected void nudgeInto(EntityIntelligent entity, Vector3 target, float push) {
        double dx = target.x - entity.x;
        double dz = target.z - entity.z;

        double len2 = dx * dx + dz * dz;
        if (len2 < 1e-6) return;

        double len = Math.sqrt(len2);
        dx /= len;
        dz /= len;

        Vector3 motion = entity.getMotion();
        entity.setMotion(new Vector3(
                motion.x + (dx * push),
                motion.y,
                motion.z + (dz * push)
        ));
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    protected void stop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        oldTargetFloor = null;

        if (entity instanceof EntityStrider s) {
            entity.setMovementSpeed(s.getDefaultSpeed());
        } else {
            entity.setMovementSpeed(0.1f);
        }

        if (clearDataWhenLose && entity.getBehaviorGroup() != null) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
    }
}
