package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;


public class BlockSlabCrimson extends BlockSlab {


    public BlockSlabCrimson() {
        this(0);
    }


    public BlockSlabCrimson(int meta) {
        super(meta, CRIMSON_DOUBLE_SLAB);
    }


    @Override
    public String getSlabName() {
        return "Crimson";
    }
    
    @Override
    public int getId() {
        return CRIMSON_SLAB;
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
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

}
