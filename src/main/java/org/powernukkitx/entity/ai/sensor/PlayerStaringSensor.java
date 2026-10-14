package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.math.Vector3;
import lombok.Getter;


@Getter
public class PlayerStaringSensor implements ISensor {

    protected double range;
    protected double triggerDiff;
    protected boolean ignoreRotation;

    public PlayerStaringSensor(double range, double triggerDiff, boolean ignoreRotation) {
        this.range = range;
        this.triggerDiff = triggerDiff;
        this.ignoreRotation = ignoreRotation;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        double rangeSquared = this.range * this.range;
        Vector3 eyePosition = entity.add(0, entity.getEyeHeight(), 0);
        for(Player player : entity.getViewers().values()) {
            if(player.distanceSquared(entity) <= rangeSquared) {
                if(ignoreRotation || Math.abs(Math.abs(player.headYaw-entity.headYaw)-180) <= this.triggerDiff) {
                    if(player.isLookingAt(eyePosition, triggerDiff, true)) {
                        entity.getMemoryStorage().put(CoreMemoryTypes.STARING_PLAYER, player);
                        return;
                    }
                }
            }
        }
        entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
    }

}
