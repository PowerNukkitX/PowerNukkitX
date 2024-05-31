package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityVex extends EntityMob implements EntityFlyable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return VEX;
    }
    /**
     * @deprecated 
     */
    

    public EntityVex(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
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
        return 0.8f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Vex";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
