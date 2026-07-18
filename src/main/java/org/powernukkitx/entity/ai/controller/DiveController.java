package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;

/**
 * 下潜运动控制器，使实体下潜
 */


public class DiveController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        //add dive force
        if (entity.getMemoryStorage().get(CoreMemoryTypes.ENABLE_DIVE_FORCE))
            //                                                                  抵消额外的浮力即可
            entity.motionY -= entity.getGravity() * (entity.getFloatingForceFactor() - 1);
        return true;
    }
}
