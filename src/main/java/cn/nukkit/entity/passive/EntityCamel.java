package cn.nukkit.entity.passive;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityCamel extends EntityAnimal {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return CAMEL;
    }
    /**
     * @deprecated 
     */
    

    public EntityCamel(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(32);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (isBaby()) {
            return 0.85f;
        }
        return 1.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (isBaby()) {
            return 1.1875f;
        }
        return 2.375f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getFootHeight() {
        if (!isBaby()) {
            return 1.5f;
        }
        return super.getFootHeight();
    }
}
