package cn.nukkit.entity.mob;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 两栖怪物 (eg: 溺尸)
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityAmphibiousMob extends EntityMob{
    public EntityAmphibiousMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
