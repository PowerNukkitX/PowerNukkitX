package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.level.generator.object.ObjectBigMushroom;
import org.jetbrains.annotations.NotNull;

public class BlockBrownMushroom extends BlockMushroom {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_MUSHROOM);
    public static final BlockDefinition DEFINITION = BlockMushroom.DEFINITION.toBuilder()
            .lightEmission(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroom(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    
    @Override
    protected ObjectBigMushroom.MushroomType getType() {
        return ObjectBigMushroom.MushroomType.BROWN;
    }
}