package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTwistingVines extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:twisting_vines", CommonBlockProperties.TWISTING_VINES_AGE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTwistingVines() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTwistingVines(BlockState blockstate) {
        super(blockstate);
    }
}