package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCopperOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_ORE);
    public static final BlockDefinition DEFINITION = BlockOre.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(3)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Copper Ore";
    }

    
    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.RAW_COPPER;
    }

    @Override
    protected float getDropMultiplier() {
        return 3;
    }

    
    }