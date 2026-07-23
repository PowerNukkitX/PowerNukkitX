package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockFurnace extends BlockLitFurnace {

    public static final BlockProperties PROPERTIES = new BlockProperties(FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockLitFurnace.DEFINITION.toBuilder()
            .lightEmission(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFurnace(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    
    }
