package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
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
        for(Player player : entity.getViewers().values()) {
            if(player.distance(entity) <= range) {
                if(ignoreRotation || Math.abs(Math.abs(player.headYaw-entity.headYaw)-180) <= this.triggerDiff) {
                    if(player.isLookingAt(entity.add(0, entity.getEyeHeight(), 0), triggerDiff, true)) {
                        entity.getMemoryStorage().put(CoreMemoryTypes.STARING_PLAYER, player);
                        return;
                    }
                }
            }
        }
        entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
    }

}
