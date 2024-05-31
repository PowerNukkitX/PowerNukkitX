package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityCaveSpider extends EntityMob implements EntityWalkable, EntityArthropod {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return CAVE_SPIDER;
    }
    /**
     * @deprecated 
     */
    

    public EntityCaveSpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Cave Spider";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
