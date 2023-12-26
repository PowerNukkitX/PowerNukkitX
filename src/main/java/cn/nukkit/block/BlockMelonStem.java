package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMelonStem extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:melon_stem", CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.GROWTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMelonStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMelonStem(BlockState blockstate) {
        super(blockstate);
    }
}