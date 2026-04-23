package cn.nukkit.entity.ai.sensor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityAnimal;


/**
 * Scans nearby players holding an item considered "food" by this entity and stores the nearest match in
 * {@link CoreMemoryTypes#NEAREST_FEEDING_PLAYER}.
 * <p><b>Start using Tempt Behaviors:</b>
 * Tempt behaviors should be implemented using
 * {@link cn.nukkit.entity.ai.executor.TemptExecutor} (ground) or
 * {@link cn.nukkit.entity.ai.executor.FloatTemptExecutor} (floating), which handle item checks and target selection
 * internally (BDS-like) based on the configured tempt item list and settings.
 * </p>
 */
@Deprecated(forRemoval = true, since = "2.0.0")
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
    @SuppressWarnings("removal")
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityAnimal entityAnimal) {
            Player player = null;
            double rangeSquared = this.range * this.range;
            double minRangeSquared = this.minRange * this.minRange;
            // Find the player within range who most recently meets the begging requirements.
            for (Player p : entity.getLevel().getPlayers().values()) {
                if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared && entityAnimal.isBreedingItem(p.getInventory().getItemInMainHand())) {
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

    @Override
    public int getPeriod() {
        return period;
    }
}