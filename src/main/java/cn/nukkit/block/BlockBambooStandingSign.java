package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBambooSign;


public class BlockBambooStandingSign extends BlockSignPost {
    public BlockBambooStandingSign() {
    }

    public int getId() {
        return BAMBOO_STANDING_SIGN;
    }

    public String getName() {
        return "Bamboo Standing Sign";
    }

    @Override
    public Item toItem() {
        return new ItemBambooSign();
    }
}