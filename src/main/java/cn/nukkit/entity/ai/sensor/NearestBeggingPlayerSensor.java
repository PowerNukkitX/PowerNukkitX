package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.NearestBeggingPlayerMemory;
import cn.nukkit.entity.passive.EntityAnimal;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestBeggingPlayerSensor implements ISensor{

    protected double range;

    protected double minRange;

    public NearestBeggingPlayerSensor(double range, double minRange) {
        this.range = range;
        this.minRange = minRange;
    }

    @Override
    public IMemory<?> sense(EntityIntelligent entity) {
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
            return new NearestBeggingPlayerMemory(player);
        }
        return new NearestBeggingPlayerMemory(null);
    }
}
