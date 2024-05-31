package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Erik Miller | EinBexiii
 */


public class EntityZoglin extends EntityMob implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return ZOGLIN;
    }
    /**
     * @deprecated 
     */
    

    public EntityZoglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(40);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Zoglin";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isUndead() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
