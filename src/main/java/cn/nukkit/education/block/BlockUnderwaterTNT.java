package cn.nukkit.education.block;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnderwaterTNT extends BlockTnt {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNDERWATER_TNT, CommonBlockProperties.EXPLODE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnderwaterTNT() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnderwaterTNT(BlockState blockstate) {
        super(blockstate);
    }
}