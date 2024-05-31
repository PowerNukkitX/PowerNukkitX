package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityPufferfish extends EntityAnimal implements EntitySwimmable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return PUFFERFISH;
    }
    /**
     * @deprecated 
     */
    

    public EntityPufferfish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Pufferfish";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.8f;
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
    
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }
}
