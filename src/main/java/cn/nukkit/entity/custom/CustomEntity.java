package cn.nukkit.entity.custom;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class CustomEntity extends Entity {
    public CustomEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return 0;
    }

    public abstract CustomEntityDefinition getDefinition();

}
