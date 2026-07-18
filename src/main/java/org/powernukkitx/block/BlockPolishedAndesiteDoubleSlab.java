package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockPolishedAndesiteDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_ANDESITE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedAndesiteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedAndesiteDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Polished Andesite";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPolishedAndesiteSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}