package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveTrapdoor extends BlockTrapdoor {
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