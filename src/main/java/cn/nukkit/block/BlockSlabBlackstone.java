package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;


public class BlockSlabBlackstone extends BlockSlab {


    public BlockSlabBlackstone() {
        this(0);
    }


    public BlockSlabBlackstone(int meta) {
        super(meta, BLACKSTONE_DOUBLE_SLAB);
    }

    @Override
    public int getId() {
        return BLACKSTONE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Blackstone Slab";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
