package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;

public class BlockWoodStrippedMangrove extends BlockWoodStripped {
    public BlockWoodStrippedMangrove() {
    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_WOOD;
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Wood";
    }

    @Override
    public WoodType getWoodType() {
        return null;
    }

    @Override
    public void setWoodType(WoodType woodType) {
    }
}
