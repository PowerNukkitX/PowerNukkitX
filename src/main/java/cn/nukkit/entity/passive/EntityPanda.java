package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityPanda extends EntityAnimal implements EntityWalkable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return PANDA;
    }
    /**
     * @deprecated 
     */
    
    

    public EntityPanda(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Panda";
    }
}
