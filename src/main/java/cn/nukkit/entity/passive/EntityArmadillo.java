package cn.nukkit.entity.passive;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityArmadillo extends EntityAnimal {
    public EntityArmadillo(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return ARMADILLO;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.42F;
        }
        return 0.7f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.39f;
        }
        return 0.65F;
    }
}
