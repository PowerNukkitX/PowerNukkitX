package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockWarpedSlab.PROPERTIES.getDefaultState();
    }

}