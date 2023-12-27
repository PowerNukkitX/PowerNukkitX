package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveWood extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_wood", CommonBlockProperties.PILLAR_AXIS, CommonBlockProperties.STRIPPED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveWood(BlockState blockstate) {
        super(blockstate);
    }
}