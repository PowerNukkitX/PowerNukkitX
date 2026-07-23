package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.UPDATE_BIT;

public class BlockAzaleaLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties(AZALEA_LEAVES, PERSISTENT_BIT, UPDATE_BIT);
    public static final BlockDefinition DEFINITION = BlockLeaves.DEFINITION.toBuilder()
            .build();

    public BlockAzaleaLeaves() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAzaleaLeaves(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockAzaleaLeaves(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public String getName() {
        return "Azalea Leaves";
    }

    
    @Override
    public boolean canHarvest(Item item) {
        return item.isShears();
    }

    @Override
    @NotNull public  BlockProperties getProperties() {
        return PROPERTIES;
    }

    /*the wood type is set to OAK only so the drop probabilities are correct, it does not mean this is actually oak*/
    @Override
    public WoodType getType() {
        return WoodType.OAK;
    }

}
