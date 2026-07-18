package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;

/**
 * 为飞行生物提供升力的运动控制器
 */


public class LiftController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        //add lift force
        if (entity.getMemoryStorage().get(CoreMemoryTypes.ENABLE_LIFT_FORCE))
            entity.motionY += entity.getGravity();
        return true;
    }
}
