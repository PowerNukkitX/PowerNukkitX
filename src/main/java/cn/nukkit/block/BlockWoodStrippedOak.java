package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;


public class BlockWoodStrippedOak extends BlockWoodStripped {

    public BlockWoodStrippedOak() {
        this(0);
    }


    public BlockWoodStrippedOak(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return STRIPPED_OAK_LOG;
    }


    @Override
    public WoodType getWoodType() {
        return WoodType.OAK;
    }
}
