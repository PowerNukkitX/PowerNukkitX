package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.item.Item;

//TODO: 需要进一步抽象
/**
 * 搜索狼最近携带食物的玩家,与{@link NearestFeedingPlayerSensor}相比它特判了Bone.
 * <p>
 * Search for wolves carrying food to the nearest player, compared to {@link NearestFeedingPlayerSensor}, which is specially awarded to Bone.
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public class WolfNearestFeedingPlayerSensor extends NearestFeedingPlayerSensor {

    public WolfNearestFeedingPlayerSensor(double range, double minRange) {
        this(range, minRange, 1);
    }

    public WolfNearestFeedingPlayerSensor(double range, double minRange, int period) {
        super(range, minRange, period);
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityAnimal entityAnimal) {
            Player player = null;
            double rangeSquared = this.range * this.range;
            double minRangeSquared = this.minRange * this.minRange;
            //寻找范围内最近满足乞食要求的玩家
            for (Player p : entity.getLevel().getPlayers().values()) {
                if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared && (p.getInventory().getItemInHand().getId() == Item.BONE || entityAnimal.isBreedingItem(p.getInventory().getItemInHand()))) {
                    if (player == null) {
                        player = p;
                    } else {
                        if (entity.distanceSquared(p) < entity.distanceSquared(player)) {
                            player = p;
                        }
                    }
                }
            }
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, player);
            return;
        }
        entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_FEEDING_PLAYER);
    }
}
