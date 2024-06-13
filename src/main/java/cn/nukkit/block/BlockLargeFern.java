package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockLargeFern extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(LARGE_FERN, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLargeFern() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockLargeFern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.FERN;
    }
}