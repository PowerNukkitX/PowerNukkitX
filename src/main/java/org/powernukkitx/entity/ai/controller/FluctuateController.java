package org.powernukkitx.entity.ai.controller;

import org.powernukkitx.entity.EntityIntelligent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller that makes entities flounder around in water
 */


public class FluctuateController implements IController {
    private boolean lastTickInWater = false;

    @Override
    public boolean control(EntityIntelligent entity) {
        if (entity.hasWaterAt(entity.getFloatingHeight())) {
            if (!lastTickInWater) lastTickInWater = true;
        } else {
            if (lastTickInWater) {
                lastTickInWater = false;
                if (entity.hasWaterAt(0)) {
                    if (ThreadLocalRandom.current().nextInt(0, 4) == 3) {// 1/3
                        entity.motionY += entity.getFloatingHeight() * 0.8;
                    } else {
                        entity.motionY += entity.getFloatingHeight() * 0.6;
                    }
                }
            }
        }
        return true;
    }
}
