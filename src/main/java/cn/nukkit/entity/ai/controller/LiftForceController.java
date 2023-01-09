package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

/**
 * 为飞行生物提供升力的运动控制器
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class LiftForceController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        //add lift force
        if (entity.getMemoryStorage().get(CoreMemoryTypes.ENABLE_LIFT_FORCE))
            entity.motionY += entity.getGravity();
        return true;
    }
}
