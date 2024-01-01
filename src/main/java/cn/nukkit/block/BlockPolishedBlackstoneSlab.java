package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneSlab(BlockState blockstate) {
        super(blockstate, POLISHED_BLACKSTONE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Polished Blackstone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem()};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6.0;
    }
}