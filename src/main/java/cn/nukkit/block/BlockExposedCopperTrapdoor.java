package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_copper_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}