package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return GUARDIAN;
    }
    /**
     * @deprecated 
     */
    

    public EntityGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Guardian";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.85f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.85f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
