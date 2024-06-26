package cn.nukkit.entity.mob;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityBreeze extends EntityMob {
    @Override @NotNull public String getIdentifier() {
        return BREEZE;
    }

    public EntityBreeze(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    public float getHeight() {
        return 1.77F;
    }

    @Override
    public float getWidth() {
        return 0.6F;
    }
}
