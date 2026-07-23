package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;

/**
 * A dive movement controller that makes the entity dive
 */


public class DiveController implements IController {
    @Override
    public boolean control(EntityIntelligent entity) {
        //add dive force
        if (entity.getMemoryStorage().get(CoreMemoryTypes.ENABLE_DIVE_FORCE))
            //                                                                  just cancel out the extra buoyancy
            entity.motionY -= entity.getGravity() * (entity.getFloatingForceFactor() - 1);
        return true;
    }
}
