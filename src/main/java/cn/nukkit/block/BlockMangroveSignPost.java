package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMangroveSign;


public class BlockMangroveSignPost extends BlockSignPost {

    public BlockMangroveSignPost() {
    }


    public BlockMangroveSignPost(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return MANGROVE_STANDING_SIGN;
    }


    @Override
    public int getWallId() {
        return MANGROVE_WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Mangrove Sign Post";
    }

    @Override
    public Item toItem() {
        return new ItemMangroveSign();
    }
}
