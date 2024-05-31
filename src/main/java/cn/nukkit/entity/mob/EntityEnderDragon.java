package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityEnderDragon extends EntityMob implements EntityFlyable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return ENDER_DRAGON;
    }
    /**
     * @deprecated 
     */
    

    public EntityEnderDragon(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 13f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 4f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        this.setMaxHealth(200);
        super.initEntity();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean applyNameTag(@NotNull Player player, @NotNull Item item) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Ender Dragon";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBoss() {
        return true;
    }
}