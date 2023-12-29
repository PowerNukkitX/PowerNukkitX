package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAcaciaSign;


public class BlockAcaciaSignPost extends BlockSignPost {

    public BlockAcaciaSignPost() {
    }


    public BlockAcaciaSignPost(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return ACACIA_STANDING_SIGN;
    }


    @Override
    public int getWallId() {
        return ACACIA_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Acacia Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemAcaciaSign();
    }
}
