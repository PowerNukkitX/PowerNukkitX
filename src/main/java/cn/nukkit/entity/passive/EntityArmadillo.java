package cn.nukkit.entity.passive;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityArmadillo extends EntityAnimal {
    /**
     * @deprecated 
     */
    
    public EntityArmadillo(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull 
    /**
     * @deprecated 
     */
    String getIdentifier() {
        return ARMADILLO;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (isBaby()) {
            return 0.42F;
        }
        return 0.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (isBaby()) {
            return 0.39f;
        }
        return 0.65F;
    }
}
