package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class LookController implements IController {

    protected boolean lookAtTarget;
    protected boolean lookAtRoute;

    public LookController(boolean lookAtTarget, boolean lookAtRoute) {
        this.lookAtTarget = lookAtTarget;
        this.lookAtRoute = lookAtRoute;
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        Vector3 lookTarget = entity.getLookTarget();
        Vector3 moveDirectionEnd = entity.getMoveDirectionEnd();

        if (lookAtRoute && entity.hasMoveDirection()) {
            //构建路径方向向量
            BVector3 bv2route = BVector3.fromPos(moveDirectionEnd.x - entity.x, moveDirectionEnd.y - entity.y, moveDirectionEnd.z - entity.z);
            entity.setYaw(bv2route.getYaw());
            if (!lookAtTarget) {
                entity.setHeadYaw(bv2route.getYaw());
                if (entity.isEnablePitch()) entity.setPitch(bv2route.getPitch());
                else entity.setPitch(0);
            }
        }
        if (lookAtTarget && lookTarget != null) {
            //构建指向玩家的向量
            BVector3 bv2player = BVector3.fromPos(lookTarget.x - entity.x, lookTarget.y - entity.y, lookTarget.z - entity.z);
            if (entity.isEnablePitch()) entity.setPitch(bv2player.getPitch());
            else entity.setPitch(0);
            entity.setHeadYaw(bv2player.getHeadYaw());
        }
        return true;
    }
}
