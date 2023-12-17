package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockSlabMangrove extends BlockSlab {


    public BlockSlabMangrove() {
        this(0);
    }


    public BlockSlabMangrove(int meta) {
        super(meta, DOUBLE_MANGROVE_SLAB);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public int getId() {
        return MANGROVE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Mangrove";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

}
