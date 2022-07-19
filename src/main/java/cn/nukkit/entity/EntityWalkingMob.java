package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 陆地行走怪物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityWalkingMob extends EntityMob {
    public EntityWalkingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
