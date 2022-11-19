package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.AboutControlExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 狼移动到主人身边.
 * <p>
 * The wolf moves to the master's side.
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public class WolfMoveToOwnerExecutor extends AboutControlExecutor {
    protected float speed;
    protected int maxFollowRangeSquared;
    protected Vector3 oldTarget;
    protected boolean updateRouteImmediatelyWhenTargetChange;

    public WolfMoveToOwnerExecutor(float speed, boolean updateRouteImmediatelyWhenTargetChange, int maxFollowRange) {
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
        if (maxFollowRange >= 0) {
            this.maxFollowRangeSquared = maxFollowRange * maxFollowRange;
        }
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);

        if (entity instanceof EntityWolf entityWolf) {
            var player = entityWolf.getServer().getPlayer(entityWolf.getOwnerName());
            if (player == null || entityWolf.isSitting()) return false;

            //获取目的地位置（这个clone很重要）
            var tmp = randomVector3(player, 2);
            if (tmp == null) return true;
            Vector3 target = tmp.clone();

            //不允许跨世界
            if (target instanceof Position position && !position.level.getName().equals(entity.level.getName()))
                return false;

            if (entityWolf.getPosition().floor().equals(oldTarget)) return false;

            var distanceSquared = entity.distanceSquared(player);
            if (distanceSquared <= maxFollowRangeSquared) {
                //更新寻路target
                setRouteTarget(entity, target);
                //更新视线target
                setLookTarget(entity, target);

                if (entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
                    entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, true);
                }

                if (updateRouteImmediatelyWhenTargetChange) {
                    var floor = target.floor();

                    if (oldTarget == null || oldTarget.equals(floor))
                        entity.getBehaviorGroup().setForceUpdateRoute(true);

                    oldTarget = floor;
                }

                if (entity.getMovementSpeed() != speed)
                    entity.setMovementSpeed(speed);

                return true;
            } else {
                var targetVector = randomVector3(player, 3);
                if (targetVector == null) return true;
                else return entityWolf.teleport(targetVector);
            }
        }
        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        //目标丢失
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(1.2f);
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        oldTarget = null;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        //目标丢失
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(1.2f);
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INTERESTED, false);
        }
        oldTarget = null;
    }

    protected Vector3 randomVector3(Entity player, int r) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(r * -1, r) + player.getFloorX();
        int z = random.nextInt(r * -1, r) + player.getFloorZ();
        double y = player.getLevel().getHighestBlockAt(x, z);
        var vector3 = new Vector3(x, y, z);
        var result = player.getLevel().getBlock(vector3);
        if (result.isSolid() && result.getId() != BlockID.AIR) return result.up();
        else return null;
    }
}
