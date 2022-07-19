package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 空中飞行怪物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityFlyingMob extends EntityMob {
    public EntityFlyingMob(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
