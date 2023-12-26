package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCarrots extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:carrots", CommonBlockProperties.GROWTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCarrots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCarrots(BlockState blockstate) {
        super(blockstate);
    }
}