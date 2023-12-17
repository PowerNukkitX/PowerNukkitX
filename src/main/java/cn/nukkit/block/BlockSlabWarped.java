package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;


public class BlockSlabWarped extends BlockSlab {


    public BlockSlabWarped() {
        this(0);
    }


    public BlockSlabWarped(int meta) {
        super(meta, WARPED_DOUBLE_SLAB);
    }


    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    public int getId() {
        return WARPED_SLAB;
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId() == slab.getId();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

}
