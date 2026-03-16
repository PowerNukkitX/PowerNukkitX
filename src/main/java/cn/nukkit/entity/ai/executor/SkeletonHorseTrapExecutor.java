package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.passive.EntitySkeletonHorse;

/**
 * Handles the skeleton horse trap mechanic.
 *
 * When a naturally spawned trapped skeleton horse detects a nearby player,
 * it triggers the trap event, spawning the lightning ambush and disabling
 * the trap state afterward.
 * 
 * @author Curse
 */
public class SkeletonHorseTrapExecutor implements IBehaviorExecutor, EntityControl {

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!(entity instanceof EntitySkeletonHorse horse)) return false;
        if (!horse.isStartedTrap()) return false;
        // Traps can only enable if natural spawn
        if (!horse.isNaturalSpawn() || !horse.isTrapEnabled()) return false;

        // Find nearest player in 10 blocks
        Player target = null;
        double best = Double.MAX_VALUE;
        int range = 10;
        int r2 = range * range;

        for (Player p : horse.getLevel().getPlayers().values()) {
            if (p == null || !p.isOnline() || p.isClosed()) continue;

            double d = p.distanceSquared(horse);
            if (d <= (double) r2 && d < best) {
                best = d;
                target = p;
            }
        }

        if (target == null) return false;

        horse.skeletonTrap();
        horse.disableTrap();
        horse.setStartTrap(false);

        return true;
    }
}
