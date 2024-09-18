package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockPurpurBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPUR_BLOCK, PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpurBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpurBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Purpur";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }
}