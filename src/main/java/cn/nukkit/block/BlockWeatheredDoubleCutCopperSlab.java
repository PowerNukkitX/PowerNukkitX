package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredDoubleCutCopperSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_double_cut_copper_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }
}