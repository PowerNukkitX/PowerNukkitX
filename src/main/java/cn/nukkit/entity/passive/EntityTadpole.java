package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityTadpole extends EntityAnimal implements EntitySwimmable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return TADPOLE;
    }
    /**
     * @deprecated 
     */
    
    public EntityTadpole(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.8f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.6f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Tadpole";
    }
}
