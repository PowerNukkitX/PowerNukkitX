package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityDropper;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;
import static org.powernukkitx.block.property.CommonBlockProperties.TRIGGERED_BIT;


public class BlockDropper extends BlockDispenser {

    public static final BlockProperties PROPERTIES = new BlockProperties(DROPPER, FACING_DIRECTION, TRIGGERED_BIT);
    public static final BlockDefinition DEFINITION = BlockDispenser.DEFINITION.toBuilder()
            .hardness(3.5)
            .resistance(3.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDropper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDropper(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Dropper";
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityDropper> getBlockEntityClass() {
        return BlockEntityDropper.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.DROPPER;
    }

    @Override
    public void dispense() {
        super.dispense();
    }

    @Override
    protected DispenseBehavior getDispenseBehavior(Item item) {
        return new DropperDispenseBehavior();
    }

    }
