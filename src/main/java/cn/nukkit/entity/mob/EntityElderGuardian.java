package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityElderGuardian extends EntityMob implements EntitySwimmable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return ELDER_GUARDIAN;
    }
    /**
     * @deprecated 
     */
    

    public EntityElderGuardian(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(80);
        super.initEntity();
        this.setDataFlag(EntityFlag.ELDER, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1.99f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.99f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Elder Guardian";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
