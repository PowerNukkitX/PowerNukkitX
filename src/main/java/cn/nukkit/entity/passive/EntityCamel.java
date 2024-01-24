package cn.nukkit.entity.passive;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityCamel extends EntityAnimal {
    @Override
    @NotNull public String getIdentifier() {
        return CAMEL;
    }

    public EntityCamel(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(32);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.85f;
        }
        return 1.7f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 1.1875f;
        }
        return 2.375f;
    }

    @Override
    public float getFootHeight() {
        if (!isBaby()) {
            return 1.5f;
        }
        return super.getFootHeight();
    }
}
