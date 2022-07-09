package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.*;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
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

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        //knockback
        if (entity.getAttackTime() > 0)
            return true;
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
            routeFinder = new AStarRouteFinder(entity, entity, target, target.level);
            routeFinder.setMaxSearchDepth(500);
        }
        if (movingNearDestination == null || (routeUpdated && routeFinder.isFinished())){
            routeUpdated = false;
            if (routeFinder.hasNext()){
                routeFinder.setNodeIndex(0);
                movingNearDestination = routeFinder.next().getVector3();
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
        if (tick >= 10 || routeFinder.getNodes().isEmpty()){
            routeFinder.asyncSearch();
            routeUpdated = true;
            tick = 0;
        }
        //等待直到路径计算完成
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
            if (entity.y < movingNearDestination.y && collidesBlocks(entity,dx, 0, dz)){
                if (entity.isOnGround()){
                    dy += entity.getJumpingHeight() * 0.43;
                }
            }
            return new Vector3(dx, dy, dz);
        }
        return Vector3.ZERO;
    }

    protected boolean collidesBlocks(EntityIntelligent entity,double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }

    protected boolean willFallAt(EntityIntelligent entity,double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz).grow(-0.1, 0, -0.1), true,
                false, block -> block.isSolid() || block instanceof BlockWater).length == 0;
    }

    //todo: remove debug
    protected static void sendParticle(String identifier, Position pos,Player[] showPlayers) {
        Arrays.stream(showPlayers).forEach(player -> {
            if (!player.isOnline())
                return;
            SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
            packet.identifier = identifier;
            packet.dimensionId = pos.getLevel().getDimension();
            packet.position = pos.asVector3f();
            try {
                player.dataPacket(packet);
            }catch (Throwable t){}
        });
    }
}
