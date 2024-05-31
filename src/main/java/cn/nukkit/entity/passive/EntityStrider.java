package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityStrider extends EntityAnimal implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return STRIDER;
    }
    /**
     * @deprecated 
     */
    

    public EntityStrider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Strider";
    }
}
