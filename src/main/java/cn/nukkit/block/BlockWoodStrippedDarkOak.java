package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;


public class BlockWoodStrippedDarkOak extends BlockWoodStripped {

    public BlockWoodStrippedDarkOak() {
        this(0);
    }


    public BlockWoodStrippedDarkOak(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return STRIPPED_DARK_OAK_LOG;
    }


    @Override
    public WoodType getWoodType() {
        return WoodType.DARK_OAK;
    }
}
