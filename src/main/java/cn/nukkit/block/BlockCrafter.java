package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrafter extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTER, CommonBlockProperties.CRAFTING, CommonBlockProperties.ORIENTATION, CommonBlockProperties.TRIGGERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrafter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrafter(BlockState blockstate) {
        super(blockstate);
    }
}