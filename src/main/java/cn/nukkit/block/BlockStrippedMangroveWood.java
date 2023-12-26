package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedMangroveWood extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_mangrove_wood", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedMangroveWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedMangroveWood(BlockState blockstate) {
        super(blockstate);
    }
}