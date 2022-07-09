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

    protected int tick = 0;

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        //获取目标位置
        Position target = (Position) entity.getBehaviorGroup().getMemory().get(memoryClazz).getData();
        if (target == null) {
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
        if (movingNearDestination == null){
            if (routeFinder.hasNext()){
                movingNearDestination = routeFinder.next().getVector3();
            }
        }
        if (movingNearDestination != null){
            lookAt(entity, movingNearDestination);
            entity.setMovementSpeed(0.3f);
            Vector3 motion = calMotion(entity);
            entity.addTmpMoveMotion(motion);
        }
        if (tick >= 20 || routeFinder.getNodes().isEmpty()){
            routeFinder.asyncSearch();
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
//            if (collidesBlocks(entity,dx, 0, dz)) {
//                if (entity.isFloating() && !collidesBlocks(dx, entity.getHeight(), dz)) {
//                    entity.shore();
//                } else if (!collidesBlocks(dx, entity.getJumpingHeight(), dz)) {
//                    entity.jump();
//                }
//            }
//            var wfa = willFallAt(dx, -entity.getJumpingHeight(), dz);
//            if (!entity.isJumping && wfa) {
//                System.out.println("WillFallAtNew: " + willFallAt(dx, -entity.getJumpingHeight(), dz));
//                entity.jump();
//            }
            return new Vector3(dx, 0, dz);
        }
        return Vector3.ZERO;
    }

    private boolean collidesBlocks(EntityIntelligent entity,double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid).length > 0;
    }

    private boolean willFallAt(EntityIntelligent entity,double dx, double dy, double dz) {
        return entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz).grow(-0.1, 0, -0.1), true,
                false, block -> block.isSolid() || block instanceof BlockWater).length == 0;
    }

    //todo: remove debug
    private static void sendParticle(String identifier, Position pos,Player[] showPlayers) {
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
