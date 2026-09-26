package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.item.EntityFireworksRocket;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;

import java.util.concurrent.ThreadLocalRandom;

public class CelebrateSurviveExecutor implements IBehaviorExecutor {

    protected final int duration;
    protected final int minFireworkInterval;
    protected final int fireworkIntervalSpread;
    protected int currentTick = 0;
    protected int nextFireworkTick = 0;

    public CelebrateSurviveExecutor(int duration, int minFireworkInterval, int fireworkIntervalSpread) {
        this.duration = duration;
        this.minFireworkInterval = minFireworkInterval;
        this.fireworkIntervalSpread = fireworkIntervalSpread;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (currentTick >= nextFireworkTick) {
            launchFirework(entity);
            nextFireworkTick = currentTick + minFireworkInterval
                    + ThreadLocalRandom.current().nextInt(fireworkIntervalSpread + 1);
        }
        return ++currentTick <= duration;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        currentTick = 0;
        nextFireworkTick = 0;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stopCelebrating(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stopCelebrating(entity);
    }

    protected void stopCelebrating(EntityIntelligent entity) {
        currentTick = 0;
        nextFireworkTick = 0;
        entity.getMemoryStorage().put(CoreMemoryTypes.CELEBRATING, false);
    }

    protected void launchFirework(EntityIntelligent entity) {
        if (entity.chunk == null) {
            return;
        }
        CompoundTag nbt = Entity.getDefaultNBT(
                new Vector3(entity.getX(), entity.getY() + entity.getHeight(), entity.getZ()));
        nbt.putCompound("FireworkItem", ItemHelper.write(Item.get(ItemID.FIREWORK_ROCKET)));
        new EntityFireworksRocket(entity.chunk, nbt).spawnToAll();
    }
}
