package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockIronOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_ORE);
    public static final BlockDefinition DEFINITION = BlockOre.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_STONE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockIronOre(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }

    
    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.RAW_IRON;
    }
}