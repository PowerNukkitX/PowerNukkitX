package cn.nukkit.block.impl;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockLog;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;

@Since("1.20.0-r2")
@PowerNukkitXOnly
public class BlockWoodCherry extends BlockLog {
    public static final String STRIPPED_BIT = "stripped_bit";

    public static final BlockProperties PROPERTIES =
            new BlockProperties(new BooleanBlockProperty(STRIPPED_BIT, true), PILLAR_AXIS);

    public BlockWoodCherry() {
        super(0);
    }

    public BlockWoodCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_WOOD;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isStripped() ? "Stripped " : "") + "Cherry Wood";
    }

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
        return BlockState.of(STRIPPED_CHERRY_WOOD).withProperty(PILLAR_AXIS, getPillarAxis());
    }
}
