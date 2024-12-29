package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import lombok.Getter;


@Getter
public class PlayerStaringSensor implements ISensor {

    protected double range;
    protected double triggerDiff;

    public PlayerStaringSensor(double range, double triggerDiff) {
        this.range = range;
        this.triggerDiff = triggerDiff;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        for(Player player : entity.getViewers().values()) {
            if(player.distance(entity) <= range) {
                if(Math.abs(Math.abs(player.headYaw-entity.headYaw)-180) <= this.triggerDiff) { //checks if enderman and player look at each other.
                    if(player.isLookingAt(entity.add(entity.getEyeHeight()), triggerDiff)) {
                        entity.getMemoryStorage().put(CoreMemoryTypes.STARING_PLAYER, player);
                        return;
                    }
                }
            }
        }
        entity.getMemoryStorage().clear(CoreMemoryTypes.STARING_PLAYER);
    }

}
