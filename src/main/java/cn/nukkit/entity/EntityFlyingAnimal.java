package cn.nukkit.entity;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 空中飞行动物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntityFlyingAnimal extends EntityAnimal {
    public EntityFlyingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
