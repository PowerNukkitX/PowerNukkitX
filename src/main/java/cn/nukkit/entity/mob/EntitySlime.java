package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob implements EntityWalkable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return SLIME;
    }
    /**
     * @deprecated 
     */
    

    public EntitySlime(IChunk chunk, CompoundTag nbt) {
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
        return 2.04f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 2.04f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SLIME_BALL)};
    }
}
