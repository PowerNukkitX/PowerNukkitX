package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryWood extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_CHERRY_WOOD, CommonBlockProperties.PILLAR_AXIS);
    public static final BlockDefinition DEFINITION = BlockWoodStripped.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(10)
            .burnChance(5)
            .burnAbility(5)
            .canBeActivated(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCherryWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCherryWood(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Stripped Cherry Wood";
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }
}