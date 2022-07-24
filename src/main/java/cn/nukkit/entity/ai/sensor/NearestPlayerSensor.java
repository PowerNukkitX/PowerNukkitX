package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

//存储最近的玩家的Memory
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class NearestPlayerSensor implements ISensor {

    protected double range;

    protected double minRange;

    public NearestPlayerSensor(double range, double minRange) {
        this.range = range;
        this.minRange = minRange;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        NearestPlayerMemory currentMemory = entity.getMemoryStorage().get(NearestPlayerMemory.class);
        Player player = null;
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        //寻找范围内最近的玩家
        for (Player p : entity.getLevel().getPlayers().values()) {
            if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared) {
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
    }
}
