package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityAllay;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

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
        if(entity instanceof EntityGolem) return false;
        if(entity instanceof EntityAllay) return false;
        return entity instanceof EntityMob;
    }
}
