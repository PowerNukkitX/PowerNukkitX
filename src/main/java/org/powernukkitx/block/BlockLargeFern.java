package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.DoublePlantType;
import org.jetbrains.annotations.NotNull;

public class BlockLargeFern extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(LARGE_FERN, CommonBlockProperties.UPPER_BLOCK_BIT);
    public static final BlockDefinition DEFINITION = BlockDoublePlant.DEFINITION.toBuilder()
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLargeFern() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockLargeFern(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.FERN;
    }
}