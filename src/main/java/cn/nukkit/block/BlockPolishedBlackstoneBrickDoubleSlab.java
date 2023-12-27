package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_brick_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}