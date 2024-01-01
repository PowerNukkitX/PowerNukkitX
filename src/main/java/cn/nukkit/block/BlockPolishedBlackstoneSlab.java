package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneSlab(BlockState blockstate) {
        super(blockstate);
    }
}