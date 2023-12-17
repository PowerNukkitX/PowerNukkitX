package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;


public abstract class BlockFrogLight extends BlockSolid{

    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.PILLAR_AXIS);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public double getResistance() {
        return 0.3;
    }
}
