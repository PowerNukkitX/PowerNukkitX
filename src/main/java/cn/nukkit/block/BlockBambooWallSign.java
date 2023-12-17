package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBambooSign;


public class BlockBambooWallSign extends BlockWallSign {
    public BlockBambooWallSign() {
    }

    public int getId() {
        return BAMBOO_WALL_SIGN;
    }

    public String getName() {
        return "Bamboo Wall Sign";
    }

    @Override
    protected int getPostId() {
        return BAMBOO_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemBambooSign();
    }
}