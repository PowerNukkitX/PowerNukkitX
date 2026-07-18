package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockPrismarineDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPrismarineDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPrismarineDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Prismarine";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPrismarineSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}