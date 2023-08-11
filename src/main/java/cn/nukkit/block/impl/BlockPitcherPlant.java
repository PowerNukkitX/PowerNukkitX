package cn.nukkit.block.impl;

import cn.nukkit.block.BlockFlowable;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.BooleanBlockProperty;
import org.jetbrains.annotations.NotNull;

public class BlockPitcherPlant extends BlockFlowable {
    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);
    public static final BlockProperties PROPERTIES = new BlockProperties(UPPER_BLOCK);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherPlant() {
    }

    public BlockPitcherPlant(int meta) {
        super(meta);
    }

    public int getId() {
        return PITCHER_PLANT;
    }

    public String getName() {
        return "Pitcher Plant";
    }
}
