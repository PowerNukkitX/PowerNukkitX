package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedMushroomBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_mushroom_block", CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedMushroomBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedMushroomBlock(BlockState blockstate) {
        super(blockstate);
    }
}