package cn.nukkit.block;

import cn.nukkit.block.property.enums.PrismarineBlockType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PRISMARINE_BLOCK_TYPE;


public class BlockPrismarine extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE, PRISMARINE_BLOCK_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPrismarine() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPrismarine(BlockState blockState) {
        super(blockState);
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
    public String getName() {
        return switch (getPrismarineBlockType()) {
            case DEFAULT -> "Prismarine";
            case DARK -> "Dark Prismarine";
            case BRICKS -> "Prismarine Bricks";
        };
    }

    public void setPrismarineBlockType(PrismarineBlockType prismarineBlockType) {
        setPropertyValue(PRISMARINE_BLOCK_TYPE, prismarineBlockType);
    }

    public PrismarineBlockType getPrismarineBlockType() {
        return getPropertyValue(PRISMARINE_BLOCK_TYPE);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getBlockState(PRISMARINE_BLOCK_TYPE.createValue(getPropertyValue(PRISMARINE_BLOCK_TYPE))).toBlock());
    }
}
