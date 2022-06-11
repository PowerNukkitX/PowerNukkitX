package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.WoodType;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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
