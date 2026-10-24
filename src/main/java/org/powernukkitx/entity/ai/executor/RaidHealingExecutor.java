package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.entity.mob.EntityWitch;

/** Throws supportive raid potions using the normal potion projectile and launch event. */
public class RaidHealingExecutor extends PotionThrowExecutor {
    /** Creates a ten-block healing behavior with a three-second throw sequence. */
    public RaidHealingExecutor() {
        super(CoreMemoryTypes.RAID_HEAL_TARGET, 0.25f, 10, true, 40, 20);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Entity ally = entity.getMemoryStorage().get(CoreMemoryTypes.RAID_HEAL_TARGET);
        var manager = entity.getLevel().getVillageManager();
        if (ally == null || ally instanceof EntityWitch || !manager.isRaider(entity) || !manager.isRaider(ally)
                || entity.distanceSquared(ally) > 100 || !entity.hasLineOfSight(ally)
                || entity.getLevel().getCurrentTick() < entity.getMemoryStorage().get(CoreMemoryTypes.RAID_HEAL_COOLDOWN)) {
            return false;
        }
        return super.execute(entity) && entity.getMemoryStorage().notEmpty(CoreMemoryTypes.RAID_HEAL_TARGET);
    }

    @Override
    public int getPotionEffect(Entity entity) {
        Entity ally = ((EntityIntelligent) entity).getMemoryStorage().get(CoreMemoryTypes.RAID_HEAL_TARGET);
        return ally != null && ally.getHealthCurrent() <= 4 ? PotionType.HEALING.id() : PotionType.REGENERATION.id();
    }

    @Override
    protected void throwPotion(EntityLiving entity) {
        super.throwPotion(entity);
        var memory = ((EntityIntelligent) entity).getMemoryStorage();
        memory.clear(CoreMemoryTypes.RAID_HEAL_TARGET);
        memory.put(CoreMemoryTypes.RAID_HEAL_COOLDOWN, entity.getLevel().getCurrentTick() + 200);
    }
}
