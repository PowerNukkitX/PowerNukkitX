package cn.nukkit.block;

import cn.nukkit.block.property.enums.PrismarineBlockType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PRISMARINE_BLOCK_TYPE;


public class BlockPrismarine extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
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
        this.blockstate = switch (prismarineBlockType) {
            case BRICKS -> BlockPrismarineBricks.PROPERTIES.getDefaultState();
            case DARK -> BlockDarkPrismarine.PROPERTIES.getDefaultState();
            case DEFAULT -> PROPERTIES.getDefaultState();
        };
    }

    public PrismarineBlockType getPrismarineBlockType() {
        return switch (this) {
            case BlockPrismarineBricks ignored -> PrismarineBlockType.BRICKS;
            case BlockDarkPrismarine ignored -> PrismarineBlockType.DARK;
            default -> PrismarineBlockType.DEFAULT;
        };
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
        return switch (this.getPrismarineBlockType()) {
            case BRICKS -> new ItemBlock(BlockPrismarineBricks.PROPERTIES.getDefaultState().toBlock());
            case DARK -> new ItemBlock(BlockDarkPrismarine.PROPERTIES.getDefaultState().toBlock());
            case DEFAULT -> new ItemBlock(this.getProperties().getDefaultState().toBlock());
        };
    }
}
