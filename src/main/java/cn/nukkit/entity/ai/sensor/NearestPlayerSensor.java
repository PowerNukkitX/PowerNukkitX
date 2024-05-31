package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import lombok.Getter;

//存储最近的玩家的Memory


@Getter
public class NearestPlayerSensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;
    /**
     * @deprecated 
     */
    

    public NearestPlayerSensor(double range, double minRange) {
        this(range, minRange, 1);
    }
    /**
     * @deprecated 
     */
    

    public NearestPlayerSensor(double range, double minRange, int period) {
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sense(EntityIntelligent entity) {
        Player $1 = null;
        double $2 = this.range * this.range;
        double $3 = this.minRange * this.minRange;
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
        entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_PLAYER, player);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPeriod() {
        return period;
    }
}
