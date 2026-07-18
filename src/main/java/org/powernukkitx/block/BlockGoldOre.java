package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGoldOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLD_ORE);
    public static final BlockDefinition DEFINITION = BlockOre.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_IRON)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Gold Ore";
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.RAW_GOLD;
    }

    }