package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;


public class EntitySnowGolem extends EntityMob implements EntityWalkable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SNOW_GOLEM;
    }
    /**
     * @deprecated 
     */
    

    public EntitySnowGolem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Snow Golem";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.4f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.8f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(4);
        super.initEntity();
    }
}
