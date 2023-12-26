package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCopperTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}