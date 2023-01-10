package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * 控制实体在水中扑腾的控制器
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class FluctuateController implements IController {
    private final double strength;
    private boolean lastTickInWater = false;

    public FluctuateController() {
        this.strength = 6d;
    }

    public FluctuateController(double strength) {
        this.strength = strength;
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.hasWaterAt(entity.getFloatingHeight())) {
            if (!lastTickInWater) lastTickInWater = true;
        } else {
            if (lastTickInWater) {
                lastTickInWater = false;
                if (entity.hasWaterAt(0)) {
                    entity.motionY += strength * entity.getGravity();
                }
            }
        }
        return true;
    }
}
