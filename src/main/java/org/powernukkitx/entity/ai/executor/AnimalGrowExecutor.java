package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Handles real-time growth progression for ageable baby entities.
 * <p>
 * Converts elapsed time into grow ticks, reduces the remaining
 * growth duration, and when fully grown, finalizes adulthood state,
 * plays growth particles, and drops configured ageable items.
 */
public class AnimalGrowExecutor implements IBehaviorExecutor {
    // TODO: Growth rate
    private static final int TPS = 20;

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isAgeable() || !entity.isBaby() || entity.isGrowthPaused()) return false;

        int left = entity.getTicksGrowLeft();
        if (left < 0) return false;

        final CompoundTag nbtMap = entity.getNbt();

        if (!nbtMap.contains(Entity.TAG_ENTITY_BIRTH_DATE)) return false;
        long birthSec = nbtMap.getLong(Entity.TAG_ENTITY_BIRTH_DATE);
        if (birthSec <= 0) return false;

        long nowSec = System.currentTimeMillis() / 1000L;

        long lastSyncSec;
        if (nbtMap.contains(EntityLiving.TAG_ENTITY_GROW_LAST_SYNC)) {
            lastSyncSec = nbtMap.getLong(EntityLiving.TAG_ENTITY_GROW_LAST_SYNC);
            if (lastSyncSec < birthSec) lastSyncSec = birthSec;
        } else {
            lastSyncSec = birthSec;
        }

        long deltaSec = nowSec - lastSyncSec;
        if (deltaSec <= 0) return false;

        // Convert elapsed time to ticks
        long deltaTicksLong = deltaSec * TPS;
        if (deltaTicksLong <= 0) return false;

        int deltaTicks = (deltaTicksLong > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) deltaTicksLong;

        entity.reduceGrowLeft(deltaTicks);
        entity.getNbt().putLong(EntityLiving.TAG_ENTITY_GROW_LAST_SYNC, nowSec);

        // If fully grown, finalize + particles + drop items
        if (entity.getTicksGrowLeft() == 0) {
            AgeableComponent ageable = entity.getComponentAgeable();
            if (ageable != null) {
                for (String id : ageable.resolvedDropItems()) {
                    if (id == null || id.isEmpty()) continue;
                    Item drop = Item.get(id, 0, 1);
                    if (drop.isNull()) continue;
                    entity.getLevel().dropItem(entity, drop);
                }
            }

            entity.setBaby(false);
            entity.playBabyGrowthParticle();
            return true;
        }

        return false;
    }
}
