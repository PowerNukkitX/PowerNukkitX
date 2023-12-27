package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:sandstone_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneStairs(BlockState blockstate) {
        super(blockstate);
    }
}