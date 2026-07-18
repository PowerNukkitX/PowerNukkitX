package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockPolishedDioriteDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DIORITE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDioriteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDioriteDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Polished Diorite";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPolishedDioriteSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}