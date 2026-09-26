package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.concurrent.ThreadLocalRandom;


public class WardenSniffExecutor implements IBehaviorExecutor {

    protected final int duration;
    protected final int minCooldown;
    protected final int maxCooldown;
    protected final int angerAddition;
    protected int endTime;
    protected int nextSniffTick;
    protected boolean running;

    public WardenSniffExecutor(int duration, int minCooldown, int maxCooldown, int angerAddition) {
        this.duration = duration;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.angerAddition = angerAddition;
    }

    /**
     * Returns whether the sniff behavior can currently run.
     *
     * @param entity behavior owner
     * @return whether the behavior can run
     */
    public boolean canRun(EntityIntelligent entity) {
        return this.running || entity.getLevel().getTick() >= this.nextSniffTick;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return entity.getLevel().getTick() < this.endTime;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.running = true;
        this.endTime = entity.getLevel().getTick() + this.duration;
        entity.setDataFlag(ActorFlags.SNIFFING, true);
        entity.level.addSound(entity.clone(), Sound.MOB_WARDEN_SNIFF);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.finishSniff(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.finishSniff(entity);
    }

    protected void finishSniff(EntityIntelligent entity) {
        if (!this.running) return;

        this.running = false;
        entity.setDataFlag(ActorFlags.SNIFFING, false);

        if (entity instanceof EntityWarden warden) {
            Player nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Player player : entity.level.getPlayers().values()) {
                if (!warden.isValidSniffTarget(player)) continue;

                double distance = entity.distanceSquared(player);
                if (distance < nearestDistance) {
                    nearest = player;
                    nearestDistance = distance;
                }
            }

            if (nearest != null) {
                warden.setSuspiciousLocation(nearest.getVector3());
                if (warden.isInSuspicionRange(nearest)) {
                    warden.addEntityAngerValue(nearest, this.angerAddition);
                }
            }
        }

        this.nextSniffTick = entity.getLevel().getTick() + ThreadLocalRandom.current().nextInt(this.minCooldown, this.maxCooldown + 1);
    }
}
