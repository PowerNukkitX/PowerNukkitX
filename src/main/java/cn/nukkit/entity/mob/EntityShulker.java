package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityShulker extends EntityMob implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SHULKER;
    }
    /**
     * @deprecated 
     */
    

    public EntityShulker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Shulker";
    }
}
