package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopperTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_copper_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}