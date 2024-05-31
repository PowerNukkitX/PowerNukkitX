package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockSoulCampfire extends BlockCampfire {
    public static final BlockProperties $1 = new BlockProperties(SOUL_CAMPFIRE, CommonBlockProperties.EXTINGUISHED, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulCampfire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulCampfire(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return isExtinguished() ? 0 : 10;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(BlockID.SOUL_SOIL, 0, 1)};
    }

    @Override
    protected EntityDamageEvent getDamageEvent(Entity entity) {
        return new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 2);
    }
}
