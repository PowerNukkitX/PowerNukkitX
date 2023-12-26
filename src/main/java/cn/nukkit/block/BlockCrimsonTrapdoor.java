package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}