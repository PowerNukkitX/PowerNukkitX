package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCobblestoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLESTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobblestoneSlab() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockCobblestoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}