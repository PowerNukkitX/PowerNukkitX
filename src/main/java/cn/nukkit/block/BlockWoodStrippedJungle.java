package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;


public class BlockWoodStrippedJungle extends BlockWoodStripped {

    public BlockWoodStrippedJungle() {
        this(0);
    }


    public BlockWoodStrippedJungle(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return STRIPPED_JUNGLE_LOG;
    }


    @Override
    public WoodType getWoodType() {
        return WoodType.JUNGLE;
    }
}
