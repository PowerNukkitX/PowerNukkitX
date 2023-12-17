package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;


public class BlockSlabBlackstonePolished extends BlockSlab {


    public BlockSlabBlackstonePolished() {
        this(0);
    }


    public BlockSlabBlackstonePolished(int meta) {
        super(meta, POLISHED_BLACKSTONE_DOUBLE_SLAB);
    }


    protected BlockSlabBlackstonePolished(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Polished Blackstone";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId() == slab.getId();
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
            return new Item[]{ toItem() };
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
