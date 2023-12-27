package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownMushroomBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_mushroom_block", CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroomBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroomBlock(BlockState blockstate) {
        super(blockstate);
    }
}