package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockWoodenButton.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedButton(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Warped Button";
    }

    
    }