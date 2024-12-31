package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityGolem extends EntityIntelligent implements EntityWalkable {

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
}
