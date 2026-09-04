package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemWarpedSign;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockSignBase.DEFINITION.toBuilder()
            .burnChance(0)
            .burnAbility(0)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedStandingSign(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getWallSignId() {
        return BlockWarpedWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemWarpedSign();
    }

    
    }