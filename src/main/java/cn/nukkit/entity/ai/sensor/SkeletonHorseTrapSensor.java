package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.passive.EntitySkeletonHorse;


/**
 * Sensor that periodically checks for nearby players to activate the
 * skeleton horse trap mechanic.
 *
 * When a player enters the configured range of a naturally spawned
 * trapped skeleton horse, it flags the entity to trigger the trap
 * behavior handled by the corresponding executor.
 * 
 * @author Curse
 */
public class SkeletonHorseTrapSensor implements ISensor {

    private final int range;
    private final int period;
    private int tick;

    public SkeletonHorseTrapSensor(int range, int period) {
        this.range  = Math.max(1, range);
        this.period = Math.max(1, period);
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (++tick % period != 0) return;
        if (!(entity instanceof EntitySkeletonHorse horse)) return;

        if (!horse.isNaturalSpawn() || !horse.isTrapEnabled()) {
            return;
        }

        Player nearest = null;
        double best = Double.MAX_VALUE;
        int r2 = range * range;

        for (Player p : horse.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || p.isClosed()) continue;

            double d = p.distanceSquared(horse);
            if (d <= (double) r2 && d < best) {
                best = d;
                nearest = p;
            }
        }

        if (nearest != null) {
            horse.setStartTrap(true);
        }
    }
}
