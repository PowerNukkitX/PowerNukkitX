package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslate extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslate() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeepslate(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Deepslate";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!canHarvest(item)) {
            return Item.EMPTY_ARRAY;
        }

        return new Item[]{Item.get(BlockID.COBBLED_DEEPSLATE)};
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
