package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MoveDirectionMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.entity.ai.memory.NeedUpdateMoveDirectionMemory;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class LookController implements IController{

    protected boolean lookAtTarget;
    protected boolean lookAtRoute;

    public LookController(boolean lookAtTarget,boolean lookAtRoute){
        this.lookAtTarget = lookAtTarget;
        this.lookAtRoute = lookAtRoute;
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.getMemoryStorage().contains(MoveTargetMemory.class) && entity.getMemoryStorage().contains(MoveDirectionMemory.class) && !entity.getMemoryStorage().contains(NeedUpdateMoveDirectionMemory.class)) {
            Vector3 target = entity.getMemoryStorage().get(MoveTargetMemory.class).getData();
            MoveDirectionMemory directionMemory = entity.getMemoryStorage().get(MoveDirectionMemory.class);
            setYawAndPitch(entity, target, directionMemory);
            return true;
        }else {
            return false;
        }
    }

    protected void setYawAndPitch(EntityIntelligent entity, Vector3 target, MoveDirectionMemory directionMemory) {
        if (lookAtRoute) {
            //构建方向向量
            BVector3 bv2route = BVector3.fromPos(directionMemory.getEnd().x - entity.x, directionMemory.getEnd().y - entity.y, directionMemory.getEnd().z - entity.z);
            entity.setYaw(bv2route.getYaw());
            if (!lookAtTarget) {
                entity.setHeadYaw(bv2route.getYaw());
                entity.setPitch(bv2route.getPitch());
            }
        }
        if (lookAtTarget) {
            //构建指向玩家的向量
            BVector3 bv2player = BVector3.fromPos(target.x - entity.x, target.y - entity.y, target.z - entity.z);
            entity.setPitch(bv2player.getPitch());
            entity.setHeadYaw(bv2player.getHeadYaw());
        }
    }
}
