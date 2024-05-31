package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityGhast extends EntityMob implements EntityFlyable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return GHAST;
    }
    /**
     * @deprecated 
     */
    

    public EntityGhast(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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
    
    public float getWidth() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Ghast";
    }
}
