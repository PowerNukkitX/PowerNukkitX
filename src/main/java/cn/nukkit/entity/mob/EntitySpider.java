package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntitySpider extends EntityMob implements EntityWalkable, EntityArthropod {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SPIDER;
    }
    /**
     * @deprecated 
     */
    
    

    public EntitySpider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 1.4f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.9f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Spider";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.STRING),Item.get(Item.SPIDER_EYE)};
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
