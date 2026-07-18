package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.passive.EntityAnimal;
import org.powernukkitx.item.Item;

/**
 * @deprecated Since 2.0.0 (2026-02-19).
 * This wolf-specific feeding sensor has been replaced by the generic
 * {@link NearestFeedingPlayerSensor}, which supports configurable item
 * checks and can be used by any entity.
 *
 * Planned removal: after 6 months (>= 2026-08-19).
 */
@Deprecated(since = "2.0.0", forRemoval = true)
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
            // Find the player within range who most recently meets the begging requirements.
            for (Player p : entity.getLevel().getPlayers().values()) {
                if (entity.distanceSquared(p) <= rangeSquared && entity.distanceSquared(p) >= minRangeSquared && (p.getInventory().getItemInMainHand().getId() == Item.BONE || entityAnimal.isBreedingItem(p.getInventory().getItemInMainHand()))) {
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