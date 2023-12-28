package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.PrismarineBlockType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockPrismarine extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:barrier", CommonBlockProperties.PRISMARINE_BLOCK_TYPE);

    public BlockPrismarine() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPrismarine(BlockState blockState) {
        super(blockState);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
        setPropertyValue(CommonBlockProperties.PRISMARINE_BLOCK_TYPE, prismarineBlockType);
    }


    public PrismarineBlockType getPrismarineBlockType() {
        return getPropertyValue(CommonBlockProperties.PRISMARINE_BLOCK_TYPE);
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
