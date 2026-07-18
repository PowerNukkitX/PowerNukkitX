package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockSandstoneDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Sandstone";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockSandstoneSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}