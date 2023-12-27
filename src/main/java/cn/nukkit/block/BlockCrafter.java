package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrafter extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crafter", CommonBlockProperties.CRAFTING, CommonBlockProperties.ORIENTATION, CommonBlockProperties.TRIGGERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrafter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrafter(BlockState blockstate) {
        super(blockstate);
    }
}