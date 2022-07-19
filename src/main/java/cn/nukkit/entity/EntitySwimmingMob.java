package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 水中游泳怪物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntitySwimmingMob extends EntityMob {
    public EntitySwimmingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
