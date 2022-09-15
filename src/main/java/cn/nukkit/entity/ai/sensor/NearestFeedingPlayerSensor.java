package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.NearestFeedingPlayerMemory;
import cn.nukkit.entity.passive.EntityAnimal;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestFeedingPlayerSensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;

    public NearestFeedingPlayerSensor(double range, double minRange) {
        this(range, minRange, 1);
    }

    public NearestFeedingPlayerSensor(double range, double minRange, int period) {
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        NearestFeedingPlayerMemory currentMemory = entity.getMemoryStorage().get(NearestFeedingPlayerMemory.class);
        if (entity instanceof EntityAnimal entityAnimal) {
            Player player = null;
            double rangeSquared = this.range * this.range;
            double minRangeSquared = this.minRange * this.minRange;
            //寻找范围内最近满足乞食要求的玩家
            for (Player p : entity.getLevel().getPlayers().values()) {
                if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared && entityAnimal.isBreedingItem(p.getInventory().getItemInHand())) {
                    if (player == null) {
                        player = p;
                    } else {
                        if (entity.distanceSquared(p) < entity.distanceSquared(player)) {
                            player = p;
                        }
                    }
                }
            }
            currentMemory.setData(player);
            return;
        }
        currentMemory.setData(null);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
