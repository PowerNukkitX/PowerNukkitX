package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;


public class BlockCrimsonSignPost extends BlockSignPost {


    public BlockCrimsonSignPost() {
        // Does nothing
    }


    public BlockCrimsonSignPost(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CRIMSON_STANDING_SIGN;
    }


    @Override
    public int getWallId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Crimson Sign Post";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CRIMSON_SIGN);
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
