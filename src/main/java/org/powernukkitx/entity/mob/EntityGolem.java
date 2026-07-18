package org.powernukkitx.entity.mob;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanAttack;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.passive.EntityAllay;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

public abstract class EntityGolem extends EntityIntelligent implements EntityWalkable, EntityCanAttack {

    public EntityGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && !(entityDamageByEntityEvent.getDamager() instanceof EntityCreeper)) {
            getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, entityDamageByEntityEvent.getDamager());
        }
        return super.attack(source);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        if (entity instanceof EntityGolem) return false;
        if (entity instanceof EntityAllay) return false;
        return entity instanceof EntityMob;
    }
}
