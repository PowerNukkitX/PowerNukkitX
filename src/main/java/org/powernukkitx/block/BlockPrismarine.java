package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.enums.PrismarineBlockType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockPrismarine extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPrismarine() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPrismarine(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockPrismarine(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
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
    public Item toItem() {
        return switch (this.getPrismarineBlockType()) {
            case BRICKS -> new ItemBlock(BlockPrismarineBricks.PROPERTIES.getDefaultState().toBlock());
            case DARK -> new ItemBlock(BlockDarkPrismarine.PROPERTIES.getDefaultState().toBlock());
            case DEFAULT -> new ItemBlock(this.getProperties().getDefaultState().toBlock());
        };
    }
}
