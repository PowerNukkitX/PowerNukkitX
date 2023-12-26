package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPumpkinStem extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pumpkin_stem", CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROWTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPumpkinStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPumpkinStem(BlockState blockstate) {
        super(blockstate);
    }
}