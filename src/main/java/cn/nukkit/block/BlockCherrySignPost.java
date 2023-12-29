package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCherrySign;


public class BlockCherrySignPost extends BlockSignPost {
    public BlockCherrySignPost() {
    }

    public BlockCherrySignPost(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CHERRY_STANDING_SIGN;
    }


    @Override
    public int getWallId() {
        return CHERRY_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Cherry Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemCherrySign();
    }
}
