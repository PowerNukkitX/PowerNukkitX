package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_trapdoor", CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}