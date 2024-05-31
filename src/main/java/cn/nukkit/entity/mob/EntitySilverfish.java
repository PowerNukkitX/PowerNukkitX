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
public class EntitySilverfish extends EntityMob implements EntityWalkable, EntityArthropod {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SILVERFISH;
    }
    /**
     * @deprecated 
     */
    

    public EntitySilverfish(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    


    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Silverfish";
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
        return 0.3f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
