package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 两栖动物 (eg: turtle)
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityAmphibiousAnimal extends EntityAnimal {
    public EntityAmphibiousAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
