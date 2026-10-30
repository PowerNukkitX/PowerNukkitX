package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.mob.EntityWitch;

/** Selects a nearby raid ally without loading chunks or targeting other witches. */
public class RaidHealingSensor implements ISensor {
    @Override
    public void sense(EntityIntelligent entity) {
        var memory = entity.getMemoryStorage();
        var level = entity.getLevel();
        var manager = level.getVillageManager();
        if (!manager.isRaider(entity) || level.getCurrentTick() < memory.get(CoreMemoryTypes.RAID_HEAL_COOLDOWN)) {
            memory.clear(CoreMemoryTypes.RAID_HEAL_TARGET);
            return;
        }
        Entity current = memory.get(CoreMemoryTypes.RAID_HEAL_TARGET);
        if (current != null && !(current instanceof EntityWitch) && manager.isRaider(current)
                && entity.distanceSquared(current) <= 100 && entity.hasLineOfSight(current)) return;
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity candidate : level.getEntities(entity, 10, false)) {
            if (candidate == entity || candidate instanceof EntityWitch || !manager.isRaider(candidate)) continue;
            double distance = entity.distanceSquared(candidate);
            if (distance <= 100 && distance < nearestDistance && entity.hasLineOfSight(candidate)) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        if (nearest == null) memory.clear(CoreMemoryTypes.RAID_HEAL_TARGET);
        else memory.put(CoreMemoryTypes.RAID_HEAL_TARGET, nearest);
    }

    @Override
    public int getPeriod() {
        return 20;
    }
}
