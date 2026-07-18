package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoalOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(COAL_ORE);
    public static final BlockDefinition DEFINITION = BlockOre.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(3)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Coal Ore";
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.COAL;
    }

    
    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3);
    }

    
    }