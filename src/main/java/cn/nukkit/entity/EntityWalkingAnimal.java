package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 陆地行走动物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityWalkingAnimal extends EntityAnimal {
    public EntityWalkingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
