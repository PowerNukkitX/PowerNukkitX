package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;

public class BlockLogStrippedMangrove extends BlockWoodStripped{
    public BlockLogStrippedMangrove() {
    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Log";
    }

    @Override
    public WoodType getWoodType() {
        return null;
    }

    @Override
    public void setWoodType(WoodType woodType) {
    }
}
