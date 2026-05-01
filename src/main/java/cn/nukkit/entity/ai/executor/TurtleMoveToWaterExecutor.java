package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Behavior that makes a Turtle move toward nearby water.
 *
 * Uses a stored water block memory as a reference, chooses a water target
 * farther inside the water body, and routes the entity toward it. When the
 * turtle is centered over the water but still treated as standing on the
 * surface, it commits the entity into the water column so swimming movement
 * can take over.
 *
 * @author Curse
 */
@Getter
public class TurtleMoveToWaterExecutor implements EntityControl, IBehaviorExecutor {
    protected final MemoryType<? extends Vector3> memory;
    protected final float speed;
    protected Vector3 oldTargetFloor;

    public TurtleMoveToWaterExecutor(MemoryType<? extends Vector3> memory, float speed) {
        this.memory = memory;
        this.speed = speed;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        var mem = entity.getBehaviorGroup().getMemoryStorage();
        if (mem.isEmpty(memory)) return false;

        Vector3 raw = mem.get(memory);
        if (raw == null) return false;
        raw = raw.clone();

        if (raw instanceof Position p && p.level != null && p.level != entity.level) return false;

        Block waterBlock = findInnerWaterBlock(entity, raw.floor());
        if (waterBlock == null) {
            stop(entity);
            return false;
        }

        Vector3 waterCenter = new Vector3(waterBlock.getFloorX() + 0.5f, waterBlock.getFloorY() + 1.0f, waterBlock.getFloorZ() + 0.5f);

        if (isSettledInWater(entity)) {
            removeRouteTarget(entity);
            removeLookTarget(entity);
            oldTargetFloor = null;
            return false;
        }

        setRouteTarget(entity, waterCenter);
        setLookTarget(entity, waterCenter);

        double dx = waterCenter.x - entity.x;
        double dz = waterCenter.z - entity.z;
        double d2xz = (dx * dx) + (dz * dz);

        if (d2xz <= (1.5 * 1.5)) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            if (d2xz <= (0.35 * 0.35) && isOverWaterColumn(entity) && !entity.isTouchingWater()) {
                commitIntoWater(entity, waterBlock);
                return true;
            }
            nudgeInto(entity, waterCenter, 0.05f);
        }

        Vector3 floor = waterCenter.floor();
        if (oldTargetFloor == null || !oldTargetFloor.equals(floor)) {
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            oldTargetFloor = floor;
        }

        if (entity.getMovementSpeed() != speed) {
            entity.setMovementSpeed(speed);
        }

        return true;
    }

    protected Block findInnerWaterBlock(EntityIntelligent entity, Vector3 rawFloor) {
        int x = rawFloor.getFloorX();
        int y = rawFloor.getFloorY();
        int z = rawFloor.getFloorZ();

        Block first = findWaterNear(entity, x, y, z);
        if (first == null) return null;

        double dx = (first.getFloorX() + 0.5) - entity.x;
        double dz = (first.getFloorZ() + 0.5) - entity.z;

        int stepX = 0;
        int stepZ = 0;
        if (Math.abs(dx) >= Math.abs(dz)) {
            stepX = dx >= 0 ? 1 : -1;
        } else {
            stepZ = dz >= 0 ? 1 : -1;
        }

        Block best = first;
        if (stepX == 0 && stepZ == 0) return best;

        for (int i = 1; i <= 3; i++) {
            Block candidate = findTopWaterNear(entity, first.getFloorX() + (stepX * i), first.getFloorY(), first.getFloorZ() + (stepZ * i));
            if (candidate != null) {
                best = candidate;
            } else {
                break;
            }
        }

        return best;
    }

    protected Block findWaterNear(EntityIntelligent entity, int x, int y, int z) {
        for (int dy = 1; dy >= -1; dy--) {
            Block block = entity.level.getBlock(x, y + dy, z);
            if (block instanceof BlockFlowingWater) return block;
        }
        return null;
    }

    protected Block findTopWaterNear(EntityIntelligent entity, int x, int y, int z) {
        Block top = null;
        for (int dy = -1; dy <= 1; dy++) {
            Block block = entity.level.getBlock(x, y + dy, z);
            if (block instanceof BlockFlowingWater) {
                if (top == null || block.getFloorY() > top.getFloorY()) {
                    top = block;
                }
            }
        }
        return top;
    }

    protected boolean isSettledInWater(EntityIntelligent entity) {
        if (!entity.isInsideOfWater() && !entity.isTouchingWater()) return false;

        return isOverWaterColumn(entity);
    }

    protected boolean isOverWaterColumn(EntityIntelligent entity) {
        int x = (int) Math.floor(entity.x);
        int z = (int) Math.floor(entity.z);
        int y = (int) Math.floor(entity.y);

        return entity.level.getBlock(x, y, z) instanceof BlockFlowingWater
                || entity.level.getBlock(x, y - 1, z) instanceof BlockFlowingWater;
    }

    protected void nudgeInto(EntityIntelligent entity, Vector3 target, float push) {
        double dx = target.x - entity.x;
        double dz = target.z - entity.z;

        double len2 = dx * dx + dz * dz;
        if (len2 < 1e-6) return;

        double len = Math.sqrt(len2);
        dx /= len;
        dz /= len;

        double y = 0.0d;
        if (isOverWaterColumn(entity) && !isSettledInWater(entity)) {
            y -= 0.06d;
        }
        Vector3 tmp = new Vector3(
                dx * push,
                y,
                dz * push
        );
        entity.addTmpMoveMotion(tmp);
    }

    protected void commitIntoWater(EntityIntelligent entity, Block waterBlock) {
        Vector3 pos = new Vector3(entity.x, waterBlock.getFloorY() + 0.05d, entity.z);
        entity.setPosition(pos);
        entity.onGround = false;
        entity.motionY = -0.02d;
        entity.updateMovement();
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
        removeLookTarget(entity);
        oldTargetFloor = null;
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }
}
