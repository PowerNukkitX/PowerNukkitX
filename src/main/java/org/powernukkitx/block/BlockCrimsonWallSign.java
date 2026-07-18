package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCrimsonSign;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockCrimsonWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_WALL_SIGN, FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockSignBase.DEFINITION.toBuilder()
            .burnChance(-1)
            .burnAbility(0)
            .build();

    public BlockCrimsonWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonWallSign(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getWallSignId() {
        return CRIMSON_WALL_SIGN;
    }

    @Override
    public String getStandingSignId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemCrimsonSign();
    }

    
    }
