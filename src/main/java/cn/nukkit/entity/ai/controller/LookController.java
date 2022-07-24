package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.LookTargetMemory;
import cn.nukkit.entity.ai.memory.MoveDirectionMemory;
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
        LookTargetMemory lookTargetMemory = entity.getMemoryStorage().get(LookTargetMemory.class);
        MoveDirectionMemory moveDirectionMemory = entity.getMemoryStorage().get(MoveDirectionMemory.class);

        if (lookAtRoute && moveDirectionMemory.hasData()) {
            //构建路径方向向量
            BVector3 bv2route = BVector3.fromPos(moveDirectionMemory.getEnd().x - entity.x, moveDirectionMemory.getEnd().y - entity.y, moveDirectionMemory.getEnd().z - entity.z);
            entity.setYaw(bv2route.getYaw());
            if (!lookAtTarget) {
                entity.setHeadYaw(bv2route.getYaw());
                entity.setPitch(bv2route.getPitch());
            }
        }
        if (lookAtTarget && lookTargetMemory.hasData()) {
            Vector3 target = lookTargetMemory.getData();
            //构建指向玩家的向量
            BVector3 bv2player = BVector3.fromPos(target.x - entity.x, target.y - entity.y, target.z - entity.z);
            entity.setPitch(bv2player.getPitch());
            entity.setHeadYaw(bv2player.getHeadYaw());
        }
        return true;
    }
}
