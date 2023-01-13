package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AngleMath;
import cn.nukkit.math.Vector3;

/**
 * 处理实体Pitch/Yaw/HeadYaw
 */
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

        if (lookAtRoute && entity.hasMoveDirection()) {
            //clone防止异步导致的NPE
            Vector3 moveDirectionEnd = entity.getMoveDirectionEnd().clone();
            //构建路径方向向量
            var routeDirectionVector = new Vector3(moveDirectionEnd.x - entity.x, moveDirectionEnd.y - entity.y, moveDirectionEnd.z - entity.z);
            var yaw = AngleMath.getYawFromVector(routeDirectionVector);
            entity.setYaw(yaw);
            if (!lookAtTarget) {
                entity.setHeadYaw(yaw);
                if (entity.isEnablePitch()) entity.setPitch(AngleMath.getPitchFromVector(routeDirectionVector));
            }
        }
        if (lookAtTarget && lookTarget != null) {
            //构建指向玩家的向量
            var toPlayerVector = new Vector3(lookTarget.x - entity.x, lookTarget.y - entity.y, lookTarget.z - entity.z);
            if (entity.isEnablePitch()) entity.setPitch(AngleMath.getPitchFromVector(toPlayerVector));
            entity.setHeadYaw(AngleMath.getYawFromVector(toPlayerVector));
        }
        if (!entity.isEnablePitch()) entity.setPitch(0);
        return true;
    }
}
