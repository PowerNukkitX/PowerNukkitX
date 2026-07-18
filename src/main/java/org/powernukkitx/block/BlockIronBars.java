package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockIronBars extends BlockThin {

    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_BARS);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(5)
            .resistance(10)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronBars() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronBars(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Iron Bars";
    }

    
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    
    }
