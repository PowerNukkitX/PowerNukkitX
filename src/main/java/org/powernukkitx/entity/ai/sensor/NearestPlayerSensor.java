package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import lombok.Getter;

//Memory that stores the nearest player


@Getter
public class NearestPlayerSensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;

    public NearestPlayerSensor(double range, double minRange) {
        this(range, minRange, 1);
    }

    public NearestPlayerSensor(double range, double minRange, int period) {
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        Player player = null;
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        //Find the nearest player within range
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
        entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_PLAYER, player);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
