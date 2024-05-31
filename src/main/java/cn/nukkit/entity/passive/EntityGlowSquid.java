package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityGlowSquid extends EntityAnimal implements EntitySwimmable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return GLOW_SQUID;
    }
    /**
     * @deprecated 
     */
    

    public EntityGlowSquid(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.95f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.475f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "GlowSquid";
    }
}
