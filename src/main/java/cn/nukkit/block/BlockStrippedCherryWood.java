package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryWood extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_cherry_wood", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCherryWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCherryWood(BlockState blockstate) {
        super(blockstate);
    }
}