package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityBat extends EntityAnimal implements EntityFlyable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return BAT;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityBat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Bat";
    }
}
