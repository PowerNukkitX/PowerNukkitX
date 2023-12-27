package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeepingVines extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weeping_vines", CommonBlockProperties.WEEPING_VINES_AGE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeepingVines() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeepingVines(BlockState blockstate) {
        super(blockstate);
    }
}