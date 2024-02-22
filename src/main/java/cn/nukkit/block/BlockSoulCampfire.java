package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSoulCampfire;
import org.jetbrains.annotations.NotNull;

public class BlockSoulCampfire extends BlockCampfire {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_CAMPFIRE, CommonBlockProperties.EXTINGUISHED, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulCampfire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulCampfire(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished() ? 0 : 10;
    }

    @Override
    public Item toItem() {
        return new ItemSoulCampfire();
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    @Override
    protected EntityDamageEvent getDamageEvent(Entity entity) {
        return new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 2);
    }
}