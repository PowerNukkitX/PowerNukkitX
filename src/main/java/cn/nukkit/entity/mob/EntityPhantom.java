package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityPhantom extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return PHANTOM;
    }
    /**
     * @deprecated 
     */
    

    public EntityPhantom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.9f;
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
        return "Phantom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(ItemID.PHANTOM_MEMBRANE)};
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
    
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
