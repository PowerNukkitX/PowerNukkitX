package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.*;
import cn.nukkit.entity.ai.route.blockevaluator.OnGroundBlockEvaluator;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;
import lombok.Getter;

import java.util.Arrays;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class WalkToTargetExecutor extends BaseMoveExecutor{

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<?> memoryClazz;

    protected AStarRouteFinder routeFinder;

    protected Vector3 movingNearDestination;

    protected boolean routeUpdated = false;

    protected int tick = 0;

    //多久更新一次路径
    protected static final int TICK_RATE = 20;

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        //获取目标位置
        Position target = (Position) entity.getBehaviorGroup().getMemory().get(memoryClazz).getData();
        if (target == null) {
            routeFinder = null;
            movingNearDestination = null;
            routeUpdated = false;
            tick = 0;
            return false;
        }
        //已到达
        if (entity.getFloorX() == target.getFloorX() && entity.getFloorY() == target.getFloorY() && entity.getFloorZ() == target.getFloorZ()) {
            return false;
        }
        if (routeFinder == null) {
            routeFinder = new AStarRouteFinder(new OnGroundBlockEvaluator(),entity, entity, target, target.level);
            routeFinder.setMaxSearchDepth(100);
            //第一次搜索
            routeFinder.asyncSearch();
            routeUpdated = true;
        }
        if (movingNearDestination == null || (routeUpdated && routeFinder.isFinished())){
            routeUpdated = false;
            routeFinder.setNodeIndex(0);
            if (routeFinder.hasNext()){
                movingNearDestination = routeFinder.next().getVector3();
            }else{
                //等待路径更新
                return true;
            }
        }
        //构建指向玩家的向量
        BVector3 bv2player = BVector3.fromPos(target.x - entity.x,target.y - entity.y + 0.5,target.z - entity.z);
        entity.setPitch(bv2player.getPitch());
        entity.setHeadYaw(bv2player.getHeadYaw());
        if (movingNearDestination != null){
            //构建指向路径的向量
            BVector3 bv2route = BVector3.fromPos(movingNearDestination.x - entity.x,movingNearDestination.y - entity.y,movingNearDestination.z - entity.z);
            entity.setYaw(bv2route.getYaw());
            entity.setMovementSpeed(0.25f);
            Vector3 motion = calMotion(entity);
            entity.addTmpMoveMotion(motion);
        }
        if (tick >= TICK_RATE || routeFinder.getNodes().isEmpty()){
            routeFinder.asyncSearch();
            routeUpdated = true;
            tick = 0;
        }
        return true;
    }

    public Vector3 calMotion(EntityIntelligent entity) {
        var vector = movingNearDestination;
        if (vector != null) {
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return Vector3.ZERO;
            }
            vector = vector.clone().setComponents(vector.x - entity.x,
                    vector.y - entity.y, vector.z - entity.z);
            var xzLength = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
            if (xzLength < speed) {
                movingNearDestination = null;
                return Vector3.ZERO;
            }
            var k = speed / xzLength * 0.33;
            var dx = vector.x * k;
            var dz = vector.z * k;
            var dy = 0d;
            if (entity.y < movingNearDestination.y && collidesImpassibleBlocks(entity,dx, 0, dz)){
                int id = entity.getLevelBlock().getId();
                if (entity.isOnGround() || (id == Block.FLOWING_WATER || id == Block.STILL_WATER)){
                    dy += entity.getJumpingHeight() * 0.43;
                }
            }
            return new Vector3(dx, dy, dz);
        }
        return Vector3.ZERO;
    }

    protected boolean collidesImpassibleBlocks(EntityIntelligent entity, double dx, double dy, double dz) {
        return Arrays.stream(entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid)).filter(block -> !block.canPassThrough()).toArray().length > 0;
    }
}
