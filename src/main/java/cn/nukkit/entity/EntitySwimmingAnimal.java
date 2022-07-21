package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 水中游泳动物
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class EntitySwimmingAnimal extends EntityAnimal {
    public EntitySwimmingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getGravity() {
        return 0f;
    }
}
