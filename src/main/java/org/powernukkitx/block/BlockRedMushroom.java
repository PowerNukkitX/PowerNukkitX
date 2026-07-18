package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.level.generator.object.ObjectBigMushroom;
import org.jetbrains.annotations.NotNull;

public class BlockRedMushroom extends BlockMushroom {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_MUSHROOM);
    public static final BlockDefinition DEFINITION = BlockMushroom.DEFINITION.toBuilder()
            .lightEmission(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedMushroom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedMushroom(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    
    @Override
    protected ObjectBigMushroom.MushroomType getType() {
        return ObjectBigMushroom.MushroomType.RED;
    }
}