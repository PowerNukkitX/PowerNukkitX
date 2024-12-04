package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockResinClump extends BlockLichen {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_CLUMP, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinClump() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinClump(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(Block.RESIN_CLUMP, 0, getGrowthSides().length)};
    }
}