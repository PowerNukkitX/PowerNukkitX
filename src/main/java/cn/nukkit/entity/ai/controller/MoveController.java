package cn.nukkit.entity.ai.controller;


import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveDestinationMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateMoveDestinationMemory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

import java.util.Arrays;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MoveController implements IController {

    @Override
    public boolean control(EntityIntelligent entity) {
        if(entity.getMemoryStorage().contains(MoveDestinationMemory.class)) {
            Vector3 target = (Vector3) entity.getMemoryStorage().get(MoveTargetMemory.class).getData();
            Vector3 vector = (Vector3) entity.getMemoryStorage().get(MoveDestinationMemory.class).getData();
            setYawAndPitch(entity, target, vector);
            var speed = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return false;
            }
            var relativeVector = vector.clone().setComponents(vector.x - entity.x,
                    vector.y - entity.y, vector.z - entity.z);
            var xzLength = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.z * relativeVector.z);
            if (xzLength < speed) {
                needNewDestination(entity);
                return false;
            }
            var k = speed / xzLength * 0.33;
            var dx = relativeVector.x * k;
            var dz = relativeVector.z * k;
            var dy = 0.0d;
            if (entity.y < vector.y && collidesImpassibleBlocks(entity,dx, 0, dz)){
                int id = entity.getLevelBlock().getId();
                if (entity.isOnGround() || (id == Block.FLOWING_WATER || id == Block.STILL_WATER)){
                    dy += entity.getJumpingHeight() * 0.43;
                }
            }
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            return true;
        }else {
            needNewDestination(entity);
            return false;
        }
    }

    protected void needNewDestination(EntityIntelligent entity) {
        //通知需要新的移动目标
        entity.getMemoryStorage().put(new NeedUpdateMoveDestinationMemory(true));
    }

    protected boolean collidesImpassibleBlocks(EntityIntelligent entity, double dx, double dy, double dz) {
        return Arrays.stream(entity.level.getCollisionBlocks(entity.getOffsetBoundingBox().getOffsetBoundingBox(dx, dy, dz), true,
                false, Block::isSolid)).filter(block -> !block.canPassThrough()).toArray().length > 0;
    }

    protected void setYawAndPitch(EntityIntelligent entity,Vector3 target,Vector3 destination){
        //构建指向玩家的向量
        BVector3 bv2player = BVector3.fromPos(target.x - entity.x,target.y - entity.y + 0.5,target.z - entity.z);
        entity.setPitch(bv2player.getPitch());
        entity.setHeadYaw(bv2player.getHeadYaw());

        //构建指向路径的向量
        BVector3 bv2route = BVector3.fromPos(destination.x - entity.x,destination.y - entity.y,destination.z - entity.z);
        entity.setYaw(bv2route.getYaw());
    }
}
