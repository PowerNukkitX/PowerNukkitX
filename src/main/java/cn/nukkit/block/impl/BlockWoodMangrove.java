package cn.nukkit.block.impl;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.BooleanBlockProperty;
import cn.nukkit.block.property.value.WoodType;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockWoodMangrove extends BlockWood {
    public static final String STRIPPED_BIT = "stripped_bit";

    public static final BlockProperties PROPERTIES =
            new BlockProperties(new BooleanBlockProperty(STRIPPED_BIT, true), PILLAR_AXIS);

    public BlockWoodMangrove() {}

    @Override
    public int getId() {
        return MANGROVE_WOOD;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isStripped() ? "Stripped " : "") + "Mangrove Wood";
    }

    @PowerNukkitOnly
    @Override
    public WoodType getWoodType() {
        return null;
    }

    @PowerNukkitOnly
    @Override
    public void setWoodType(WoodType woodType) {}

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isStripped() {
        return getBooleanValue(STRIPPED_BIT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStripped(boolean stripped) {
        setBooleanValue(STRIPPED_BIT, stripped);
    }

    @PowerNukkitOnly
    @Override
    public BlockState getStrippedState() {
        return BlockState.of(STRIPPED_MANGROVE_WOOD).withProperty(PILLAR_AXIS, getPillarAxis());
    }
}
