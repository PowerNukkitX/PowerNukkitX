package cn.nukkit.entity.passive;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntitySniffer extends EntityAnimal {
    /**
     * @deprecated 
     */
    

    public EntitySniffer(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull 
    /**
     * @deprecated 
     */
    String getIdentifier() {
        return SNIFFER;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (isBaby()) {
            return 0.855F;
        }
        return 1.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (isBaby()) {
            return 0.7875F;
        }
        return 1.75f;
    }
}
