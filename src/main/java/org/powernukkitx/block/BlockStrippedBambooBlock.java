package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PILLAR_AXIS;


public class BlockStrippedBambooBlock extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_BAMBOO_BLOCK, PILLAR_AXIS);
    public static final BlockDefinition DEFINITION = BlockWoodStripped.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(15)
            .burnChance(5)
            .burnAbility(20)
            .canBeActivated(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedBambooBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedBambooBlock(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public String getName() {
        return "Stripped Bamboo Block";
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    }