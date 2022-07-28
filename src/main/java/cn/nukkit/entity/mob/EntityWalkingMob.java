package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 陆地行走怪物
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityWalkingMob extends EntityMob {
    public EntityWalkingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
