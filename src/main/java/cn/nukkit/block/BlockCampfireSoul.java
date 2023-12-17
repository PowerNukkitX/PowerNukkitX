package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.MinecraftItemID;


public class BlockCampfireSoul extends BlockCampfire {


    public BlockCampfireSoul() {
        this(0);
    }


    public BlockCampfireSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_CAMPFIRE_BLOCK;
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished()? 0 : 10;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SOUL_CAMPFIRE);
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { MinecraftItemID.SOUL_SOIL.get(1) };
    }


    @Override
    protected EntityDamageEvent getDamageEvent(Entity entity) {
        return new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 2);
    }
}
