package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityGoat extends EntityAnimal implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return GOAT;
    }
    /**
     * @deprecated 
     */
    

    public EntityGoat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return this.isBaby() ? 0.65f : 1.3f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return this.isBaby() ? 0.45f : 0.9f;
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
        return "Goat";
    }
}
