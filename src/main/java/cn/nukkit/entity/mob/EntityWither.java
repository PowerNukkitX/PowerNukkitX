package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityWither extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return WITHER;
    }
    /**
     * @deprecated 
     */
    

    public EntityWither(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1.0f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 3.0f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(600);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Wither";
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

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBoss() {
        return true;
    }
}
