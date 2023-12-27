package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFlowingLava extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:flowing_lava", CommonBlockProperties.LIQUID_DEPTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFlowingLava() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFlowingLava(BlockState blockstate) {
        super(blockstate);
    }
}